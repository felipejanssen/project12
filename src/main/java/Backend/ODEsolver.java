package Backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import Utils.vec;

//Euler Solver
public class ODEsolver {

    public ODEsolver(BiFunction<Double, double[], double[]> ode) {
        this.ode = ode;
    }

    private BiFunction<Double, double[], double[]> ode;

    // Step calculation, repeated for every iteration call in eulerSolve, returns
    // the next state of the vector
    private double[] eulerStep(double time, double[] xCurrent, double h) {

        double[] dxdt = ode.apply(time, xCurrent);
        double[] xNext = new double[xCurrent.length];

        for (int i = 0; i < xCurrent.length; i++) {
            xNext[i] = xCurrent[i] + h * dxdt[i];
        }
        return xNext;
    }

    // Main function to solve the ODE, returns the final state of the vector
    public double[] eulerSolve(int steps, double time0, double[] xCurrent, double h) {

        double[] x = xCurrent.clone();
        double time = time0;

        for (int i = 0; i < steps; i++) {
            x = eulerStep(time, x, h);
            time += h;
        }
        return x;
    }

    /**
     * Euler solvr with save
     * 
     * @param steps
     * @param time0
     * @param xCurrent
     * @param h
     * @param fileName
     * @return
     */
    public double[] eulerSolve(int steps, double time0, double[] xCurrent, double h, String fileName) {

        double[] x = xCurrent.clone();
        double time = time0;

        for (int i = 0; i < steps; i++) {
            x = eulerStep(time, x, h);
            time += h;
        }
        return x;
    }

    // Runge-Kutta 4
    /**
     * Estimates the results of given ODE using RK4 with following parameters
     * 
     * @param steps        number of steps
     * @param time0        starting time
     * @param initialState initial state
     * @param h            time step
     * @param fileName     creates a file with given name under "src/main/java/Data"
     *                     if provided
     * @return results of ODE at end time = (time0+steps*h)
     */
    public double[] RK4Solve(int steps, double time0, double[] initialState, double h, String fileName) {
        // Check for valid step size
        if (h == 0) {
            throw new IllegalArgumentException("Step size cannot be zero");
        }

        int n = initialState.length;
        double[] results = initialState.clone();
        double t = time0;

        String defaultDirectory = "src/main/java/Data";

        // Create file path if a name was provided
        File filePath;
        if (fileName != null && !fileName.isEmpty()) {
            filePath = new File(defaultDirectory, fileName);
        } else {
            // If no file name provided, throw an exception
            throw new IllegalArgumentException("File name must be provided.");
        }

        // Write header to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            String header = "Time," + IntStream.range(0, initialState.length)
                    .mapToObj(i -> "Var" + (i + 1)) // Generating names for each variable
                    .reduce((var1, var2) -> var1 + "," + var2) // Combine the variable names into a single line
                    .orElse("");
            writer.write(header + "\n");

            for (int i = 0; i < steps; i++) {
                // Write current time and state to file
                String[] resultsToWrite = new String[n];
                for (int j = 0; j < n; j++)
                    resultsToWrite[j] = String.format("%.4f", results[j]);
                String resultLine = String.format("%.4f", t) + "," + String.join(",", resultsToWrite);
                writer.write(resultLine + "\n");

                // RK4 step to get new state
                results = RK4Step(t, results, h);
                t += h;
            }

            // Append final state
            String[] resultsToWrite = new String[n];
            for (int j = 0; j < n; j++)
                resultsToWrite[j] = String.format("%.4f", results[j]);
            String resultLine = String.format("%.4f", t) + "," + String.join(",", resultsToWrite);
            writer.write(resultLine + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        return results;
    }

    public double[] RK4Solve(int steps, double time0, double[] initialState, double h) {

        // Check for valid step size
        if (h == 0) {
            throw new IllegalArgumentException("Step size cannot be zero");
        }

        double[] results = initialState.clone();
        double t = time0;

        for (int i = 0; i < steps; i++) {
            results = RK4Step(t, results, h);
            t += h;
        }

        return results;
    }

    /**
     * Step function for RK4 solver
     * 
     * @param time0        starting time
     * @param initialState init state
     * @param h            time step
     * @return results at time0+h
     */
    private double[] RK4Step(double time0, double[] initialState, double h) {

        int n = initialState.length;
        double[] temp = new double[n];
        double[] k1 = ode.apply(time0, initialState);

        double h_half = 0.5 * h;

        temp = vec.add(initialState, vec.multiply(k1, h_half));
        double[] k2 = ode.apply(time0 + h_half, temp);

        temp = vec.add(initialState, vec.multiply(k2, h_half));
        double[] k3 = ode.apply(time0 + h_half, temp);

        temp = vec.add(initialState, vec.multiply(k3, h));
        double[] k4 = ode.apply(time0 + h, temp);

        double[] newState = new double[n];
        for (int i = 0; i < n; i++) {
            newState[i] = initialState[i] + (h / 6) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
        }

        return newState;
    }

}
