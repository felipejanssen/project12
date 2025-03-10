# Tasks for Period 4: ODE Solvers Implementation

---

## 1. Project Setup & Infrastructure
- Initialize Java project with Maven/Gradle.
- Create Git repository with `.gitignore` for Java/IDE files.
- Set up package structure:
    - `solver` (Euler, RK4, interfaces)
    - `model` (ODE systems, e.g., Lotka-Volterra)
    - `gui` (plotting, input fields)
    - `utils` (input handling, validation)
    - `tests` (JUnit tests).
- Document project structure in `README.md`.

---

## 2. Core ODE Solvers Implementation

### Task 2.1: Euler Method
- Implement `EulerSolver.java` with method:
  ```java
  public double[][] solve(ODEFunction ode, double[] y0, double tStart, double tEnd, double h)
  ```  
- Ensure it handles systems up to 10 dimensions.

### Task 2.2: Runge-Kutta 4 (RK4)
- Implement `RK4Solver.java` with similar method signature.
- Validate intermediate steps (k1–k4) with unit tests.

---

## 3. ODE Model Implementation

### Task 3.1: Test Models
- Create classes for test systems (e.g., `LotkaVolterraModel.java`):
  ```java
  public class LotkaVolterraModel implements ODEFunction {
      public double[] call(double t, double[] y) { ... }
  }
  ```  
- Include parameters (α, β, γ, δ) as configurable fields.

### Task 3.2: Analytical Solution Model
- Implement a simple ODE with known solution (e.g., exponential decay `dy/dt = -ky`) for accuracy testing.

---

## 4. Input Module & GUI

### Task 4.1: Parameter Input
- Create GUI components (JavaFX/Swing) to input:
    - Step size (`h`), solver type, integration time.
    - Initial conditions (e.g., text fields for `y0[0], y0[1], ...`).

### Task 4.2: Visualization
- Plot results using `JFreeChart` or `JavaFX Canvas`.
- Display time evolution of variables (e.g., prey vs. predator for Lotka-Volterra).

---

## 5. Testing & Validation

### Task 5.1: Unit Tests
- Write JUnit tests for Euler/RK4 against analytical solutions.
  ```java
  @Test
  public void testExponentialDecayEuler() { ... }
  ```  
- Ensure coverage for edge cases (e.g., `h=0`, large `h`).

### Task 5.2: Test System Validation
- Run Lotka-Volterra/FitzHugh-Nagumo and generate time-evolution plots for the report.

---

## 6. Performance Experiment

### Task 6.1: Error vs. Step Size
- For exponential decay:
    - Compute numerical solutions with varying `h` (e.g., 0.1, 0.05, 0.01).
    - Calculate max absolute error against `y(t) = e^{-kt}`.

### Task 6.2: Timing Analysis
- Measure CPU time for Euler/RK4 across step sizes.
- Generate log-log plots (error vs. `h`, time vs. `h`) using Python `matplotlib` or Java libraries.

---

## 7. Unit Test Quiz Preparation

### Task 7.1: Modular ODE Integration
- Create an `ODEFunction` interface to allow users to add new systems:
  ```java
  public interface ODEFunction {
      double[] call(double t, double[] y);
  }
  ```  
- Document how to implement custom ODEs (e.g., via a `CustomModel.java` template).

### Task 7.2: Input Flexibility
- Allow GUI input for custom ODEs (e.g., equation parser) or code injection.

---

## 8. Documentation & Report

### Task 8.1: Code Documentation
- Add Javadoc comments for all classes/methods.
- Write a user guide for running simulations.

### Task 8.2: Report Section
- Describe solver implementations.
- Include test system plots and log-log experiment results.

---




**Labels**: `Core`, `GUI`, `Testing`, `Infrastructure`, `Documentation`, `Experiment`