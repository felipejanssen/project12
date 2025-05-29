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
            writer.write("iteration,error,currentInitImpulse");
            writer.newLine();
            double lastDistanceMeters = 0;
            for (int iter = 0; iter < maxIterations; iter++) {
                System.out.println();
                System.out.println("Optimizer iteration: " + iter);
                currentBest = update(currentBest);

                double currentCost = computeCost(currentBest);
                double fuel = currentBest.stream().mapToDouble(Impulse::getFuelCost).sum();
                double actualDistanceMeters = penaltyFor(currentBest); // This is the real distance in meters
                double actualDistanceKm = actualDistanceMeters / 1000.0; // This is the real distance in km

                System.out.println("Distance to Titan: " + String.format("%.1f", actualDistanceKm) + " km");
                System.out.println("Fuel cost: " + fuel);
                System.out.println("Total cost: " + currentCost);
                System.out.println("Distance improvement this iteration: " + String.format("%.1f",
                        (lastDistanceMeters - actualDistanceMeters)/1000.0) + " km");
                lastDistanceMeters = penaltyFor(currentBest);


                double error = Math.abs(lastCost - currentCost);
                // System.out.println("Error: " + Math.abs(lastCost - currentCost));

                double[] currentInitImpulse = currentBest.get(0).getImpulseVec();
                StringBuilder impulseStr = new StringBuilder();
                for (int i = 0; i < currentInitImpulse.length; i++) {
                    impulseStr.append(currentInitImpulse[i]);
                    if (i < currentInitImpulse.length - 1) {
                        impulseStr.append(" ");
                    }
                }
                // System.out.println("Init: " + Arrays.toString(currentInitImpulse));
                // Write data to file
                writer.write(iter + "," + error + "," + impulseStr.toString());
                writer.newLine();

                // Titan's orbit is approximately at 52800km radius
                double actualDistanceKmm = penaltyFor(currentBest) / 1000.0;
                if (actualDistanceKmm < 300) { // 300 km from Titan, not 300,000 cost units
                    System.out.println("In orbit");
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
     * Perform one iteration’s worth of updates on the impulse list.
     * Concrete subclasses must implement this.
     */
    protected abstract List<Impulse> update(List<Impulse> impulses);

    /**
     * Default cost: sum of fuel plus penalty on the resulting trajectory.
     * You can override if you need something more exotic.
     */
    protected double computeCost(List<Impulse> impulses) {
        double fuel = impulses.stream().mapToDouble(Impulse::getFuelCost).sum();
        double distanceMeters = penaltyFor(impulses);



        // Balance all three objectives
        double cost = fuel * 0.001 + distanceMeters;

        return cost;
    }

//    private double smartVelocityPenalty(List<Impulse> impulses) {
//        Trajectory[] trajectories = simulator.simulate(impulses);
//        Trajectory shipTraj = trajectories[0];
//
//        double totalPenalty = 0.0;
//        final double MAX_VELOCITY = 60.0;
//        final double SOFT_LIMIT = 55.0; // Start penalizing before hard limit
//
//        for (State state : shipTraj.getStates()) {
//            double speed = vec.magnitude(state.getVel());
//
//            if (speed > MAX_VELOCITY) {
//                // Heavy penalty for exceeding hard limit
//                double violation = speed - MAX_VELOCITY;
//                totalPenalty += violation * violation * 1000000; // Quadratic penalty!
//            } else if (speed > SOFT_LIMIT) {
//                // Gentle penalty approaching the limit
//                double approach = (speed - SOFT_LIMIT) / (MAX_VELOCITY - SOFT_LIMIT);
//                totalPenalty += approach * approach * 100000; // Encourage staying below 55 m/s
//            }
//        }
//
//        return totalPenalty;
//    }

    /**
     * Hook for computing the trajectory penalty; subclasses must supply it.
     */
    protected double penaltyFor(List<Impulse> impulses) {

        Trajectory[] trajectories = simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];
        Trajectory titanTrajectory = trajectories[1];

        State lastShipState = shipTraj.getLastState();
        double[] lastShipPos = lastShipState.getPos();
        State lastTitanState = titanTrajectory.getLastState();
        double[] lastTitanPos = lastTitanState.getPos();
        // System.out.println("Titan pos: " + Arrays.toString(lastTitanPos));
        double rawDist = vec.euclideanDistance(lastTitanPos, lastShipPos);
        double penalty = Math.log1p(rawDist);
        // System.out.println("Penalty: " + penalty);
        // System.out.println();
        return rawDist;
    }
}
