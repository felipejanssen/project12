package Tests;

import Backend.ODEsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class SolverComparisonTest {

    // this test compares accuracy and speed of euler and rk4 for the known analytical solution of dy/dt = -y and y(0)=1 which is y(t) = e ^ (-t)

    public static void main(String[] args) {

        // the ode is y' = -y
        BiFunction<Double, double[], double[]> ode = (t, state) -> new double[] {-state[0]};

        double T = 1.0;
        double analyticalSolution = Math.exp(-T);

        // step sizes to check
        double[] stepSizes = {0.1, 0.05, 0.02, 0.01, 0.005, 0.002, 0.001};

    // print statements
        System.out.println("Comparing Euler and RK4 solvers for dy/dt = -y, y(0)=1 at T=1");
        System.out.println("Analytical solution at T=1: " + analyticalSolution);
        System.out.printf("%-10s %-15s %-15s %-15s %-15s%n", "StepSize", "Euler Error", "Euler Time(ms)", "RK4 Error", "RK4 Time(ms)");
        System.out.println("---------------------------------------------------------------------------------------");

        ODEsolver solver = new ODEsolver(ode);

        int numRuns = 100000;

        // run for each step size
        for (double h : stepSizes) {
            int steps = (int) Math.round(T / h);

            // euler average
            double totalEulerTime = 0;
            double[] resultEuler = null;
            for (int i = 0; i < numRuns; i++) {
                double[] initialStateEuler = {1.0}; // reset initial state for each run
                long startEuler = System.nanoTime();
                resultEuler = solver.eulerSolve(steps, 0.0, initialStateEuler, h);
                long elapsedEuler = System.nanoTime() - startEuler;
                totalEulerTime += elapsedEuler;
            }
            double avgEulerTimeMs = totalEulerTime / (numRuns * 1e6);
            double errorEuler = Math.abs(resultEuler[0] - analyticalSolution);

            // rk4 average
            double totalRK4Time = 0;
            double[] resultRK4 = null;
            for (int i = 0; i < numRuns; i++) {
                double[] initialStateRK4 = {1.0}; // reset initial state for each run
                long startRK4 = System.nanoTime();
                resultRK4 = solver.RK4Solve(steps, 0.0, initialStateRK4, h);
                long elapsedRK4 = System.nanoTime() - startRK4;
                totalRK4Time += elapsedRK4;
            }
            double avgRK4TimeMs = totalRK4Time / (numRuns * 1e6);
            double errorRK4 = Math.abs(resultRK4[0] - analyticalSolution);

            // print the results for the current step size
            System.out.printf("%-10.4f %-15.8f %-15.4f %-15.8f %-15.4f%n", h, errorEuler, avgEulerTimeMs, errorRK4, avgRK4TimeMs);
        }
    }
}
