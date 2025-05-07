package Backend.Optimizers;
import java.util.List;

import Backend.Physics.Impulse;

public abstract class AbstractOptimizer implements OptimizerInt {
    protected final int maxIterations;
    protected final double tolerance;
    protected final double learningRate;      // you may ignore in HC
    protected double finiteDifference;  // you may ignore in HC

    protected AbstractOptimizer(int maxIterations,
                                double tolerance,
                                double learningRate,
                                double finiteDifference)
    {
        this.maxIterations   = maxIterations;
        this.tolerance       = tolerance;
        this.learningRate    = learningRate;
        this.finiteDifference= finiteDifference;
    }

    @Override
    public double optimize(List<Impulse> impulses) {
        double lastCost = Double.POSITIVE_INFINITY;
        double cost    = computeCost(impulses);

        for (int iter = 0; iter < maxIterations; iter++) {
            step(impulses);                  // ← algorithm-specific tweak
            cost = computeCost(impulses);

            if (Math.abs(lastCost - cost) < tolerance) {
                break;
            }
            lastCost = cost;
        }

        return cost;
    }

    /**
     * Perform one iteration’s worth of updates on the impulse list.
     * Concrete subclasses must implement this.
     */
    protected abstract void step(List<Impulse> impulses);

    /**
     * Default cost: sum of fuel plus penalty on the resulting trajectory.
     * You can override if you need something more exotic.
     */
    protected double computeCost(List<Impulse> impulses) {
        double fuel = impulses.stream()
                .mapToDouble(Impulse::getFuelCost)
                .sum();
        // hook out to simulator: subclasses can set this.simulator
        return fuel + penaltyFor(impulses);
    }

    /**
     * Hook for computing the trajectory penalty; subclasses must supply it.
     */
    protected abstract double penaltyFor(List<Impulse> impulses);
}
