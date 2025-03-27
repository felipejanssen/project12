package Tests;

import java.util.Arrays;
import java.util.function.BiFunction;

import Backend.ODEsolver;

/**
 * A class to test if our solvers are working as expected.
 * DON'T CHANGE ANY 'private static final' VALUE
 */
public class LVTest {

    // Lotka-Volterra model
    @SuppressWarnings("unused")
    private static final BiFunction<Double, double[], double[]> lotkaVolterra = (t, state) -> {
        double x = state[0]; // Prey population
        double y = state[1]; // Predator population

        double alpha = 0.1; // Prey birth rate
        double beta = 0.02; // Predation rate
        double delta = 0.01; // Predator reproduction efficiency
        double gamma = 0.1; // Predator death rate

        double dxdt = alpha * x - beta * x * y;
        double dydt = delta * x * y - gamma * y;

        return new double[] { dxdt, dydt };
    };

    // Initial conditions
    private static final double[] initialState = { 40.0, 9.0 };
    private static final double time0 = 0.0;
    private static final double stepSize = 0.01;
    private static final int steps = 1000;

    // Solver
    private static final ODEsolver solver = new ODEsolver(lotkaVolterra);

    /**
     * Final values should be estimately:
     * x = 2.8969 //prey
     * y = 17.849 //predator
     */
    // Expected result:
    private static final double[] expected = new double[] { 2.8969, 17.849 };

    public static void main(String[] args) {
        testRK4();
    }

    /**
     * Test run for Euler using Lotka-Volterra and predefined variables
     * 
     * @return results
     */
    @SuppressWarnings("unused")
    private static double[] testEuler() {
        double[] result = solver.eulerSolve(steps, time0, initialState, stepSize);
        System.out.println("Expected: " + Arrays.toString(expected));
        System.out.println("Actual result: " + Arrays.toString(result));
        return result;
    }

    /**
     * Test run for RK4 using Lotka-Volterra and predefined variables
     * 
     * @return results
     */
    private static double[] testRK4() {
        double[] result = solver.RK4Solve(steps, time0, initialState, stepSize, "test");
        System.out.println("Expected: " + Arrays.toString(expected));
        System.out.println("Actual result: " + Arrays.toString(result));
        return result;
    }
}
