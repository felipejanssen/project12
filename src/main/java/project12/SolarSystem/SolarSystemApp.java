package project12.SolarSystem;

import Backend.SolarSystem.Planet;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class SolarSystemApp extends Application {
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;

    @Override
    public void start(Stage stage) throws Exception {
        Planet Sun = new Planet(0,0,0,0,0,0, 50, 1.99*Math.pow(10, 30), "SUN2k.jpg");



        Group group = new Group();
        group.getChildren().add(Sun);

        Scene scene = new Scene(group, WIDTH, HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Solar System");
        stage.show();
    }
}
