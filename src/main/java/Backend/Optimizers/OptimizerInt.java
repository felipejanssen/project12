// Backend/Optimization/Optimizer.java
package Backend.Optimizers;

import Backend.Physics.Impulse;
import java.util.List;

/**
 * A generic optimizer that tweaks a list of Impulses to minimize some cost.
 */
public interface OptimizerInt {
    /**
     * Runs the optimization on the given impulses.
     * @param impulses the sequence of impulse maneuvers to tune
     * @return the best cost found
     */
    double optimize(List<Impulse> impulses);
}