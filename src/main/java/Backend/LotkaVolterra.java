package Backend;

import java.util.function.BiFunction;

public class LotkaVolterra {
    private final double alpha; // Prey birth rate
    private final double beta;  // Predation rate
    private final double gamma; // Predator death rate
    private final double delta; // Predator reproduction rate

    public LotkaVolterra(double alpha, double beta, double gamma, double delta) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
    }

    public BiFunction<Double, double[], double[]> getODEFunction() {
        return (time, state) -> {
            double x = state[0]; // Prey population
            double y = state[1]; // Predator population

            double dxdt = alpha * x - beta * x * y;
            double dydt = delta * x * y - gamma * y;

            return new double[]{dxdt, dydt};
        };
    }
}
