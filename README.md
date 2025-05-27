# ODE Solver Application

## Project Overview

This application provides a comprehensive environment for solving and visualizing Ordinary Differential Equations (ODEs) using different numerical methods. It features a graphical user interface built with JavaFX and a robust backend implementation of various ODE solvers and predefined mathematical models.

## Table of Contents

- [Project Structure](#project-structure)
- [Backend Components](#backend-components)
    - [ODE Solvers](#ode-solvers)
    - [Predefined Models](#predefined-models)
    - [Parsers and Utilities](#parsers-and-utilities)
- [GUI Components](#gui-components)
    - [Navigation and Workflow](#navigation-and-workflow)
    - [Result Visualization](#result-visualization)
- [Running the Application](#running-the-application)
- [Example Usage](#example-usage)

## Key Components

### Backend Implementation

The backend consists of various ODE solvers and mathematical models:

#### Numerical Methods

1. **Euler Method**
    - First-order numerical approximation
    - Simple implementation with linear steps
    - Lower accuracy but computationally efficient
    - Implemented in `ODEsolver.eulerSolve()`

2. **Runge-Kutta 4 (RK4) Method**
    - Fourth-order method with higher accuracy
    - Better stability for complex systems
    - More computationally intensive
    - Implemented in `ODEsolver.RK4Solve()`

#### Predefined Mathematical Models

1. **Lotka-Volterra Model** (`LotkaVolterra.java`)
    - Classic predator-prey population dynamics
    - Two coupled differential equations
    - Parameters for growth rates and interaction

2. **FitzHugh-Nagumo Model** (`FitzHughNagumo.java`)
    - Simplified neuronal activity model
    - Simulates action potential dynamics
    - Shows excitable system behavior

3. **SIR Model** (`SIRmodel.java`)
    - Compartmental model for disease spread
    - Tracks Susceptible, Infected, and Recovered populations
    - Shows epidemic progression dynamics

4. **Solar System Simulation** (`SolarSystemSimulation.java`)
    - N-body gravitational simulation
    - Models planetary motion

#### Parsers and Utilities

- **ODEParser**: Converts string equations to executable functions
- **StringVectorParser**: Parses vector input from text fields
- **vec**: Vector mathematics utility class

### GUI Components

The application uses JavaFX for its user interface:

#### Main Application Structure

- **DifferentialSolverApp**: Main entry point launching the JavaFX application
- **MenuController**: Handles global navigation between different screens
- **HomePageController**: Starting page with solver and model selection

#### Input Screens

- **EulerController**: Configuration for Euler method parameters
- **RKController**: Configuration for Runge-Kutta 4 parameters
- **customController**: Interface for entering custom ODE systems

#### Visualization

- **GraphController**: Displays solution trajectories
    - Line charts showing variable evolution over time
    - Supports comparison between different solution methods
    - Loads data from generated CSV files

## User Workflow

1. **Select Solver and Model**:
    - Choose a numerical method (Euler or Runge-Kutta 4)
    - Select a predefined model or "Custom" option

2. **Configure Parameters**:
    - For predefined models: Set model-specific parameters
    - For custom models: Enter differential equations and initial conditions
    - Set simulation parameters (step size, duration)

3. **Run Simulation**:
    - Calculation is performed using selected numerical method
    - Results are saved to a CSV file

4. **Visualize Results**:
    - View trajectory plots
    - Compare solutions from different methods

## Technical Details

### System Requirements

- Java 11 or higher
- JavaFX 11 or higher

### Running the Application

Execute the main application class: (`GUIs.DifferentialSolver.DifferentialSolverApp.java`)