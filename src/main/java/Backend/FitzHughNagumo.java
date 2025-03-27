package Backend;

import java.util.function.BiFunction;

public class FitzHughNagumo {
    private final double a;      // Parameter for the threshold of excitability
    private final double b;      // Parameter that influences the recovery rate
    private final double epsilon; // Time scale of the recovery variable
    private final double I;      // External input current

    public FitzHughNagumo(double a, double b, double epsilon, double I) {
        this.a = a;
        this.b = b;
        this.epsilon = epsilon;
        this.I = I;
    }

    public BiFunction<Double, double[], double[]> getODEFunction() {
        return (time, state) -> {
            double v = state[0]; // Membrane potential (voltage)
            double w = state[1]; // Recovery variable

            // FitzHugh-Nagumo ODEs
            double dvdt = v - (v * v * v) / 3 - w + I; // dv/dt
            double dwdt = epsilon * (v + a - b * w);  // dw/dt

            // Return the derivatives as an array
            return new double[]{dvdt, dwdt};
        };
    }
}
