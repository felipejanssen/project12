package Tests;
import java.util.function.BiFunction;

import Backend.ODE.ODEsolver;

public class EulerTest {
    public static void main(String[] args) {
        //I'm gonna be testing for dx/dt = -x
        BiFunction<Double, double[], double[]> ode = new BiFunction<Double, double[], double[]>()
        {
            @Override
            public double[] apply(Double time, double[] x) {
                double[] dxdt = new double[x.length];
                for (int i = 0; i < x.length; i++) {
                    dxdt[i] = -x[i];
                }
                return dxdt;
            }
        };

        //Testing parameters
        double time0 = 0.0; //Initial time (t0)
        double[] x0 = {1.0}; //Initial state of the vector
        double h = 0.1; //Time step
        int steps = 100; //Nmbr of steps

        ODEsolver solver = new ODEsolver(ode);
        double[] result = solver.eulerSolve(steps, time0, x0, h);

        //Analytical solution: x(t) = e^(-t) (exact value for dx/dt = -x)
        //It is used to test the error (Euler progressively gets linearly worse and worse as it goes)
        double analytical = Math.exp(-steps * h);

        //test result
        System.out.println("Numerical solution (Euler): " + result[0]);
        System.out.println("Analytical solution (Exact): " + analytical);
        System.out.println("Calculated error: " + Math.abs(result[0] - analytical));
    }
}