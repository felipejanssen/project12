package Backend.Physics;

import Backend.ODE.ODEsolver;
import java.util.function.BiFunction;

/**
 * This calculates trajectories for system of celestial bodies.
 * each body has a state vector and gravitational accelerations are computed using Newton's law of universal gravitation
 */
public class CelestialTrajectoryCalculator {

    private static final double G = 6.67430e-20;
    private static final int DIM = 6;
    public static double[] computeGravitationalDerivatives(double t, double[] state, double[] masses) {
       int amountBodies = masses.length;
       int stateSize = state.length;
       double[] derivativeState = new double[stateSize];

       for (int i = 0; i < amountBodies; i++) { // fill velocity parts of derivative vector
           int idx = i * DIM;
           derivativeState[idx] = state[idx + 3];
           derivativeState[idx + 1] = state[idx + 4];
           derivativeState[idx + 2] = state[idx + 5];
       }

       for (int i = 0; i < amountBodies; i++) { // use gravitational laws to calculate acceleration part of the derivative vector
           int idx = i * DIM;
           double xi = state[idx];
           double yi = state[idx + 1];
           double zi = state[idx + 2];
           double ax = 0;
           double ay = 0;
           double az = 0;

           for (int j = 0; j < amountBodies; j++) {
               if (i == j) continue;
               int idxj = j * DIM;
               double xj = state[idxj];
               double yj = state[idxj + 1];
               double zj = state[idxj + 2];

               // vector from i to j
               double dx = xj - xi;
               double dy = yj - yi;
               double dz = zj - zi;
               double rSquared = dx * dx + dy * dy + dz * dz;
               double r = Math.sqrt(rSquared);

               // avoid dividing by zero when bodies are very close
               if (r < 1e-5) continue;
               double acceleration = G * masses[j] / (rSquared * r);

               ax += acceleration * dx;
               ay += acceleration * dy;
               az += acceleration * dz;
           }
           derivativeState[idx + 3] = ax;
           derivativeState[idx + 4] = ay;
           derivativeState[idx + 5] = az;
       }
       return derivativeState;
    }

    // use rk4 on state vector
    public static double[] calculateTrajectory(double[] initialState, double[] masses, double t0, double timeStep, int steps) {
        BiFunction<Double, double[], double[]> derivativeFunction = (t, state) -> computeGravitationalDerivatives(t, state, masses);
        ODEsolver solver = new ODEsolver(derivativeFunction);
        return solver.RK4Solve(steps, t0, initialState, timeStep);
    }
}
