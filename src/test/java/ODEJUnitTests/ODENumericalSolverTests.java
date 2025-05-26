package ODEJUnitTests;

import Backend.ODE.ODEsolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BiFunction;

/**
 * JUnit tests for numerical ODE solving methods.
 * Tests include:
 * Euler method
 * Runge-Kutta 4 (RK4) method
 * Comparisons between solvers
 */
public class ODENumericalSolverTests {

    private ODEsolver solver;

    @BeforeEach
    public void setUp() {
        // Initialize a new solver before each test
        solver = new ODEsolver(getSimpleLinearODE());
    }

    /**
     * Simple linear ODE: dy/dt = -y
     * Analytical solution: y(t) = y0*e^(-t)
     */
    private BiFunction<Double, double[], double[]> getSimpleLinearODE() {
        return (t, y) -> new double[] { -y[0] };
    }

    /**
     * Simple 2D system: dx/dt = y, dy/dt = -x
     * Represents harmonic oscillator
     * Analytical solution: x(t) = x0*cos(t) + y0*sin(t), y(t) = y0*cos(t) - x0*sin(t)
     */
    private BiFunction<Double, double[], double[]> getHarmonicOscillatorODE() {
        return (t, state) -> {
            double x = state[0];
            double y = state[1];
            return new double[] { y, -x };
        };
    }

    /**
     * Test Euler method with a simple linear ODE where analytical solution is known
     */
    @Test
    public void testEulerWithSimpleLinearODE() {
        double[] initial = { 1.0 }; // y(0) = 1
        double startTime = 0.0;
        double stepSize = 0.1;
        int steps = 10; // Will solve up to t = 1.0

        double[] result = solver.eulerSolve(steps, startTime, initial, stepSize);

        // Analytical solution at t = 1.0 is e^(-1) ≈ 0.36788
        double analyticalSolution = Math.exp(-1.0);
        assertEquals(analyticalSolution, result[0], 0.1); // Euler has higher error, use larger tolerance
    }

    /**
     * Test RK4 method with the same simple linear ODE
     */
    @Test
    public void testRK4WithSimpleLinearODE() {
        double[] initial = { 1.0 }; // y(0) = 1
        double startTime = 0.0;
        double stepSize = 0.1;
        int steps = 10; // Will solve up to t = 1.0

        double[] result = solver.RK4Solve(steps, startTime, initial, stepSize);

        // Analytical solution at t = 1.0 is e^(-1) ≈ 0.36788
        double analyticalSolution = Math.exp(-1.0);
        assertEquals(analyticalSolution, result[0], 0.01); // RK4 should be much more accurate
    }

    /**
     * Test Euler method with 2D harmonic oscillator
     */
    @Test
    public void testEulerWithHarmonicOscillator() {
        ODEsolver oscillatorSolver = new ODEsolver(getHarmonicOscillatorODE());
        double[] initial = { 1.0, 0.0 }; // x(0) = 1, y(0) = 0
        double startTime = 0.0;
        double stepSize = 0.01;
        int steps = 628; // Approximately 2pi = 6.28, one complete period

        double[] result = oscillatorSolver.eulerSolve(steps, startTime, initial, stepSize);

        // After one period (2pi), the solution should return close to the starting point
        // Due to Euler method errors, use relaxed tolerance
        assertEquals(initial[0], result[0], 0.2);
        assertEquals(initial[1], result[1], 0.2);
    }

    /**
     * Test RK4 method with 2D harmonic oscillator
     */
    @Test
    public void testRK4WithHarmonicOscillator() {
        ODEsolver oscillatorSolver = new ODEsolver(getHarmonicOscillatorODE());
        double[] initial = { 1.0, 0.0 }; // x(0) = 1, y(0) = 0
        double startTime = 0.0;
        double stepSize = 0.01;
        int steps = 628; // Approximately 2pi = 6.28, one complete period

        double[] result = oscillatorSolver.RK4Solve(steps, startTime, initial, stepSize);

        // After one period (2pi), the solution should return close to the starting point
        // RK4 should be more accurate than Euler
        assertEquals(initial[0], result[0], 0.05);
        assertEquals(initial[1], result[1], 0.05);
    }

    /**
     * Compare Euler and RK4 solutions to verify that RK4 is more accurate.
     * Uses van der Pol oscillator which is more challenging for numerical solvers.
     */
    @Test
    public void testCompareEulerAndRK4() {
        // Van der Pol oscillator: dx/dt = y, dy/dt = μ(1-x²)y - x
        // μ parameter controls nonlinearity
        double mu = 1.0;
        BiFunction<Double, double[], double[]> vanDerPolODE = (t, state) -> {
            double x = state[0];
            double y = state[1];
            return new double[] { y, mu * (1 - x * x) * y - x };
        };

        ODEsolver vanDerPolSolver = new ODEsolver(vanDerPolODE);
        double[] initial = { 0.5, 0.0 };
        double startTime = 0.0;
        double stepSize = 0.01;
        int steps = 100; // Run for 1 time unit

        double[] eulerResult = vanDerPolSolver.eulerSolve(steps, startTime, initial, stepSize);
        double[] rk4Result = vanDerPolSolver.RK4Solve(steps, startTime, initial, stepSize);

        // Run with a much smaller step size as a reference solution
        double smallStepSize = 0.001;
        int referenceSteps = 1000;
        double[] referenceResult = vanDerPolSolver.RK4Solve(referenceSteps, startTime, initial, smallStepSize);

        // Calculate error metrics
        double eulerError = Math.sqrt(Math.pow(eulerResult[0] - referenceResult[0], 2) +
                Math.pow(eulerResult[1] - referenceResult[1], 2));

        double rk4Error = Math.sqrt(Math.pow(rk4Result[0] - referenceResult[0], 2) +
                Math.pow(rk4Result[1] - referenceResult[1], 2));

        // Verify RK4 is more accurate than Euler
        assertTrue(rk4Error < eulerError,
                "RK4 error (" + rk4Error + ") should be less than Euler error (" + eulerError + ")");
        System.out.println("Euler result: " + eulerResult[0] + ", " + eulerResult[1]);
        System.out.println("RK4 result: " + rk4Result[0] + ", " + rk4Result[1]);
        System.out.println("Reference result: " + referenceResult[0] + ", " + referenceResult[1]);
        System.out.println("Euler error: " + eulerError);
        System.out.println("RK4 error: " + rk4Error);

    }
}
