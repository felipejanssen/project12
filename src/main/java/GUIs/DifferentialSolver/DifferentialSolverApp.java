package GUIs.DifferentialSolver;

import javafx.application.Application;
import javafx.scene.image.Image;

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
        Image icon = new Image(getClass().getResource("/images/G.png").toString());
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Projet 1-2 Group 3 - Differential Equation Solver");
        ODESolverGUI gui = new ODESolverGUI();
        gui.start(primaryStage);
    }
}