package project12.DifferentialSolver;

import javafx.application.Application;

/**
 * Main entry point for the Differential Equation Solver application.
 */
public class DifferentialSolverApp extends Application {

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) {
        ODESolverGUI gui = new ODESolverGUI();
        gui.start(primaryStage);
    }
}