package Tests;

import java.util.Arrays;
import java.util.function.BiFunction;

import Backend.ODEsolver;

// Fitzhugh-Nagumo tester using RK4 (can change to euler if you want ofcourse)

public class FHNTest {
    // dV/dt = V - (V^3 / 3) - W + Iext, dW/dt = ε * (V + a - b * W)

    private static final BiFunction<Double, double[], double[]> fitzhughNagumo = (t, state) -> {
        double V = state[0];
        double W = state[1];

        // possible paramters
        double epsilon = 0.05;
        double a = 1.0;
        double b = 1.0;
        double Iext = 0.5;

        double dVdt = V - (Math.pow(V, 3) / 3.0) - W + Iext;
        double dWdt = epsilon * (V + a - b * W);

        return new double[] { dVdt, dWdt };
    };

    // create solver instance

    private static final ODEsolver solver = new ODEsolver(fitzhughNagumo);

    public static void main(String[] args) {
        testFHN();
    }


    // run model using rk4 and save results to file and print final state

    private static void testFHN() {
        double[] initialState = {-1.0, 1.0};

        double time0 = 0.0;
        double stepSize = 0.1;
        int steps = 5000;

        double[] finalState = solver.RK4Solve(steps, time0, initialState, stepSize, "FHN_results.csv");

        System.out.println("Final state after " + steps + " steps: " + Arrays.toString(finalState));
    }
}
