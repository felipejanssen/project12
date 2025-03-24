package project12.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DifferentialSolverApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DifferentialSolverApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 527);
        stage.setTitle("Project 1-2 Group 3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}