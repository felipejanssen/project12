package Backend;
import java.util.function.BiFunction;
//A bifunction takes time as the first argument, and a current vector of a single planet as a second argument

public class ODEsolver {

    public ODEsolver(BiFunction<Double, double[], double[]> ode){
        this.ode = ode;
    }

    private BiFunction<Double, double[], double[]> ode;
    private double[] function;

    public double [] eulerStep(double time, double[] xCurrent, double h ){
        double[] dxdt = ode.apply(time, xCurrent);
        double[] xNext = new double[xCurrent.length];
        for(int i = 0; i < xCurrent.length; i++){
            xNext[i] = xCurrent[i] + h * dxdt[i];
        }
        return xNext;
    }

    public double[] eulerSolve(int steps, double time0, double[] xCurrent, double h){
        double[] x = xCurrent.clone();
        double time = time0;

        for(int i = 0; i < steps; i++){
            x = eulerStep(time, x, h);
            time += h;
        }
        return x;
    }

}
