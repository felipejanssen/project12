package Backend.TestModels;

import java.util.function.BiFunction;

public class SIRmodel {
    private final double k;      // Transmission rate
    private final double gamma;  // Recovery rate
    private final double mu;     // Birth/death rate

    public SIRmodel(double k, double gamma, double mu) {
        this.k = k;
        this.gamma = gamma;
        this.mu = mu;
    }

    public BiFunction<Double, double[], double[]> getODEFunction() {
        return (time, state) -> {
            double S = state[0]; // Susceptible fraction
            double I = state[1]; // Infected fraction
            double R = state[2]; // Recovered fraction

            double dSdt = -k * S * I + mu * (1 - S);
            double dIdt = k * S * I - (gamma + mu) * I;
            double dRdt = gamma * I - mu * R;

            return new double[]{dSdt, dIdt, dRdt};
        };
    }
}
