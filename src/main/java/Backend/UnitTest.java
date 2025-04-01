package Backend;

import java.util.Arrays;
import java.util.function.BiFunction;

public class UnitTest {
    private final double sigma; // Prandtl number
    private final double rho;   // Rayleigh number
    private final double beta;  // Physical property of the system

    public UnitTest(double sigma, double rho, double beta) {
        this.sigma = sigma;
        this.rho = rho;
        this.beta = beta;
    }

    public BiFunction<Double, double[], double[]> getODEFunction() {
        return (time, state) -> {
            double x = state[0];
            double y = state[1];
            double z = state[2];

            double dxdt = sigma * (y - x);
            double dydt = x * (rho - z) - y;
            double dzdt = x * y - beta * z;

            return new double[]{dxdt, dydt, dzdt};
        };
    }
    public static void main(String[] args) {
        UnitTest test = new UnitTest(10, 30, 3);
        ODEsolver solver = new ODEsolver(test.getODEFunction());
        double[] initialVector = {0.1, 0.5, 0.2};
        double start = 0;
        double stepsize = 0.05;
        int totalSteps = 200;
        double[] results = solver.RK4Solve(totalSteps, start, initialVector, stepsize);
        System.out.println(Arrays.toString(results));
    }
}
