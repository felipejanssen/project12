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

    private final SolarSystemSimulator simulator = new SolarSystemSimulator();

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

            for (int iter = 0; iter < maxIterations; iter++) {
                System.out.println();
                System.out.println("Optimizer iteration: " + iter);
                currentBest = update(currentBest);

                double currentCost = computeCost(currentBest);
                double distanceKm = currentCost / 1000.0;
                System.out.println("Distance to Titan: " + String.format("%.1f", distanceKm) + " km");

                double fuel = currentBest.stream().mapToDouble(Impulse::getFuelCost).sum();
                System.out.println("Fuel cost: " + fuel);
                System.out.println("Total cost (should be fuel + distance): " + (fuel + distanceKm));
                System.out.println("Distance improvement this iteration: " + String.format("%.1f", (lastCost - currentCost)/1000.0) + " km");


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
                if (currentCost < 300000) {
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
        double penalty = penaltyFor(impulses);
        double cost = fuel + penalty;
        return cost;  //
    }

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
