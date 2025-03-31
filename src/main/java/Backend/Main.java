package Backend;

import java.util.Arrays;
import java.util.function.BiFunction;

public class Main {
    public static void main(String[] args) {
        BiFunction<Double, double[], double[]> testODE = (time, state) -> {
            double[] dxdt = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                dxdt[i] = 0.1 * state[i]; //simple decay function for testing ODE solver
            }
            return dxdt;
        };
        double[] initialVector = {10};
        ODEsolver solver = new ODEsolver(testODE);
        double h = 0.1;
        int steps = 100;
        double time0 = 0;

        double[] eurlerResult = solver.eulerSolve(steps, time0, initialVector, h);
        System.out.println("Initial Vector: " + Arrays.toString(initialVector) + "\n\n" + "EurlerSolution: " + Arrays.toString(eurlerResult));

        LotkaVolterra func = new LotkaVolterra(0.1, 0.02, 0.1, 0.01);
        ODEsolver solver2 = new ODEsolver(func.getODEFunction());
        double[] result = solver2.eulerSolve(steps, time0, initialVector, h);
        System.out.println("Initial Vector: " + Arrays.toString(initialVector) + "\n\n" + "EurlerSolution: " + Arrays.toString(result));
    }
}
