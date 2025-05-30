package Backend.ODE;

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

    private final BiFunction<Double, double[], double[]> ode;

    // Euler's Solver
    /**
     * Estimates the results of given ODE using Euler's Solver with following
     * parameters
     *
     * @param steps        number of steps
     * @param time0        starting time
     * @param initialState initial state of vector
     * @param h            time step
     * @param fileName     creates a file with given name under "src/main/java/Data"
     *                     if provided
     * @return results of ODE at end time = (time0+steps*h)
     */
    public double[] eulerSolve(int steps, double time0, double[] initialState, double h, String fileName) {

        int n = initialState.length;
        double[] results = initialState.clone();
        double t = time0;

        String defaultDirectory = "src/main/java/Data";

        // Create file path if a name was provided
        File filePath;
        if (fileName != null && !fileName.isEmpty()) {
            String temp = fileName + ".csv";
            filePath = new File(defaultDirectory, temp);
        } else {
            // If no file name provided, throw an exception
            throw new IllegalArgumentException("File name must be provided.");
        }

        // Write header to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            String header = "Time," + IntStream.range(0, n)
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
                results = eulerStep(t, results, h);
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

    /**
     * Step function for Euler's solver
     *
     * @param time         current time
     * @param currentState init state
     * @param h            time step
     *
     * @return results at time0+h
     */
    private double[] eulerStep(double time, double[] currentState, double h) {

        double[] dxdt = ode.apply(time, currentState);
        double[] xNext = new double[currentState.length];

        for (int i = 0; i < currentState.length; i++) {
            xNext[i] = currentState[i] + h * dxdt[i];
        }
        return xNext;
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
    public double[] eulerSolve(int steps, double time0, double[] xCurrent, double h) {
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
     * @param initialState initial state of vector
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
            String temp = fileName + ".csv";
            filePath = new File(defaultDirectory, temp);
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
     * @param time         current time
     * @param currentState current state
     * @param h            time step
     *
     * @return results at time+h
     */
    public double[] RK4Step(double time, double[] currentState, double h) {

        int n = currentState.length;
        double[] temp;
        double[] k1 = ode.apply(time, currentState);

        double h_half = 0.5 * h;

        temp = vec.add(currentState, vec.multiply(k1, h_half));
        double[] k2 = ode.apply(time + h_half, temp);

        temp = vec.add(currentState, vec.multiply(k2, h_half));
        double[] k3 = ode.apply(time + h_half, temp);

        temp = vec.add(currentState, vec.multiply(k3, h));
        double[] k4 = ode.apply(time + h, temp);

        double[] newState = new double[n];
        for (int i = 0; i < n; i++) {
            newState[i] = currentState[i] + (h / 6) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
        }

        return newState;
    }

}
