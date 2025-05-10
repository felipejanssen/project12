package Backend.Optimizers;

import java.util.ArrayList;
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

        List<Impulse> currentBest = new ArrayList<>();
        double lastCost = computeCost(impulses);

        for (int iter = 0; iter < maxIterations; iter++) {
            System.out.println("Optimizer iteration: " + iter);
            currentBest = update(impulses);

            double currentCost = computeCost(impulses);
            System.out.println("Error: " + Math.abs(lastCost - currentCost));
            if (Math.abs(lastCost - currentCost) < tolerance) {
                break;
            }
            lastCost = currentCost;
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
        double fuel = impulses.stream()
                .mapToDouble(Impulse::getFuelCost)
                .sum();
        // hook out to simulator: subclasses can set this.simulator
        double penalty = penaltyFor(impulses);
        double cost = fuel + penalty;
        // System.out.println("Cost: " + cost);
        return cost;
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

        double penalty = vec.euclideanDistance(lastTitanPos, lastShipPos);
        // System.out.println("Penalty: " + penalty);
        return penalty; // or inline your penalty fn
    }
}
