package Backend.Optimizers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Backend.Physics.Impulse;
import Backend.Physics.State;
import Backend.Physics.Trajectory;
import Backend.SolarSystem.SolarSystemSimulator;
import Utils.vec;

public abstract class AbstractOptimizer implements OptimizerInt {
    protected final int maxIterations;
    protected final double tolerance;
    protected final double learningRate; // you may ignore in HC
    protected double epsilon; // you may ignore in HC

    public final SolarSystemSimulator simulator = new SolarSystemSimulator();

    protected AbstractOptimizer(int maxIterations,
                                double tolerance,
                                double learningRate,
                                double epsilon) {
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
        this.learningRate = learningRate;
        this.epsilon = epsilon;
        System.out.println("IT: " + this.maxIterations + ", Tol: " + this.tolerance + ", LR: " + this.learningRate
                + ", E" + this.epsilon);
    }

    @Override
    public List<Impulse> optimize(List<Impulse> impulses) {

        // Create deep copy
        List<Impulse> currentBest = new ArrayList<>();
        for (Impulse impulse : impulses) {
            currentBest.add(impulse.clone());
        }

        double lastCost = computeCost(currentBest);

        String directoryPath = "src/main/java/Data/GradientDescent";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // create directories if they don't exist
        }

        // Find a unique filename
        String baseName = "GD_optimization";
        String extension = ".csv";
        int counter = 1;
        File file = new File(directory, baseName + extension);
        while (file.exists()) {
            file = new File(directory, baseName + "_" + counter + extension);
            counter++;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write CSV header
            writer.write("iteration,error,currentInitImpulse,velocityViolations,maxSpeed");
            writer.newLine();
            double lastDistanceMeters = 0;
            for (int iter = 0; iter < maxIterations; iter++) {
                System.out.println();
                System.out.println("Optimizer iteration: " + iter);
                currentBest = update(currentBest);

                double currentCost = computeCost(currentBest);
                double fuel = currentBest.stream().mapToDouble(Impulse::getFuelCost).sum();
                double actualDistanceMeters = getDistanceToTitan(currentBest); // Pure distance
                double actualDistanceKm = actualDistanceMeters / 1000.0;
                double velocityPenalty = balancedVelocityPenalty(currentBest);
                double maxSpeed = getMaxTrajectorySpeed(currentBest);
                int violations = countSpeedViolations(currentBest);

                System.out.println("Distance to Titan: " + String.format("%.1f", actualDistanceKm) + " km");
                System.out.println("Fuel cost: " + fuel);
                System.out.println("Velocity penalty: " + String.format("%.0f", velocityPenalty));
                System.out.println("Max speed in trajectory: " + String.format("%.2f", maxSpeed) + " m/s");
                System.out.println("Speed violations: " + violations);
                System.out.println("Total cost: " + currentCost);
                System.out.println("Distance improvement this iteration: " + String.format("%.1f",
                        (lastDistanceMeters - actualDistanceMeters)/1000.0) + " km");
                lastDistanceMeters = actualDistanceMeters;

                double error = Math.abs(lastCost - currentCost);

                double[] currentInitImpulse = currentBest.get(0).getImpulseVec();
                StringBuilder impulseStr = new StringBuilder();
                for (int i = 0; i < currentInitImpulse.length; i++) {
                    impulseStr.append(currentInitImpulse[i]);
                    if (i < currentInitImpulse.length - 1) {
                        impulseStr.append(" ");
                    }
                }

                // Write data to file with velocity info
                writer.write(iter + "," + error + "," + impulseStr.toString() + "," + violations + "," + String.format("%.2f", maxSpeed));
                writer.newLine();

                // Titan's orbit is approximately at 52800km radius
                if (actualDistanceKm < 300) { // 300 km from Titan
                    System.out.println("🎯 SUCCESS! In orbit around Titan!");
                    break;
                }

                if (error < tolerance) {
                    System.out.println("Diminishing return");
                    break;
                }
                lastCost = currentCost;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentBest;
    }

    /**
     * Perform one iteration's worth of updates on the impulse list.
     * Concrete subclasses must implement this.
     */
    protected abstract List<Impulse> update(List<Impulse> impulses);

    /**
     * BALANCED cost function - strong velocity penalty but allows optimization progress
     */
    protected double computeCost(List<Impulse> impulses) {
        double fuel = impulses.stream().mapToDouble(Impulse::getFuelCost).sum();
        double distanceMeters = getDistanceToTitan(impulses);
        double velocityPenalty = balancedVelocityPenalty(impulses);

        // Scale velocity penalty to be significant but not overwhelming
        double cost = fuel * 0.001 + distanceMeters + velocityPenalty * 0.1;

        return cost;
    }

    /**
     * Get pure distance to Titan without any penalties
     */
    private double getDistanceToTitan(List<Impulse> impulses) {
        Trajectory[] trajectories = simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];
        Trajectory titanTrajectory = trajectories[1];

        State lastShipState = shipTraj.getLastState();
        double[] lastShipPos = lastShipState.getPos();
        State lastTitanState = titanTrajectory.getLastState();
        double[] lastTitanPos = lastTitanState.getPos();

        return vec.euclideanDistance(lastTitanPos, lastShipPos);
    }

    /**
     * BALANCED velocity penalty - strong enough to prevent violations,
     * but allows speed optimization within limits
     */
    private double balancedVelocityPenalty(List<Impulse> impulses) {
        Trajectory[] trajectories = simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];

        double totalPenalty = 0.0;
        final double MAX_VELOCITY = 60.0;
        final double SOFT_LIMIT = 55.0; // More reasonable soft limit
        final double EFFICIENCY_TARGET = 45.0; // Encourage efficient speeds

        for (State state : shipTraj.getStates()) {
            double speed = vec.magnitude(state.getVel());

            if (speed > MAX_VELOCITY) {
                // MASSIVE penalty for hard violations - this should almost never happen
                double violation = speed - MAX_VELOCITY;
                totalPenalty += violation * violation * 50000000; // Quadratic, but huge multiplier
                System.out.println("🚨 HARD VIOLATION: " + String.format("%.2f", speed) + " m/s");
            } else if (speed > SOFT_LIMIT) {
                // Strong penalty approaching the limit - discourages getting close
                double approach = (speed - SOFT_LIMIT) / (MAX_VELOCITY - SOFT_LIMIT);
                totalPenalty += approach * approach * 5000000; // Quadratic scaling
            } else if (speed > EFFICIENCY_TARGET) {
                // Gentle penalty for inefficient speeds - allows optimization
                double inefficiency = (speed - EFFICIENCY_TARGET) / (SOFT_LIMIT - EFFICIENCY_TARGET);
                totalPenalty += inefficiency * inefficiency * 100000; // Much gentler
            }
            // No penalty below 45 m/s - allows free optimization in efficient range
        }

        return totalPenalty;
    }

    /**
     * Get the maximum speed in the entire trajectory
     */
    private double getMaxTrajectorySpeed(List<Impulse> impulses) {
        Trajectory[] trajectories = simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];

        double maxSpeed = 0.0;
        for (State state : shipTraj.getStates()) {
            double speed = vec.magnitude(state.getVel());
            maxSpeed = Math.max(maxSpeed, speed);
        }
        return maxSpeed;
    }

    /**
     * Count how many states exceed the speed limit
     */
    private int countSpeedViolations(List<Impulse> impulses) {
        Trajectory[] trajectories = simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];

        int violations = 0;
        final double MAX_VELOCITY = 60.0;

        for (State state : shipTraj.getStates()) {
            double speed = vec.magnitude(state.getVel());
            if (speed > MAX_VELOCITY) {
                violations++;
            }
        }
        return violations;
    }

    /**
     * Hook for computing the trajectory penalty; subclasses must supply it.
     * NOW ONLY RETURNS DISTANCE - velocity penalty is handled separately
     */
    protected double penaltyFor(List<Impulse> impulses) {
        return getDistanceToTitan(impulses);
    }
}