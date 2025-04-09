package project12.SolarSystem;

import Backend.SolarSystem.Planet;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SolarSystemApp extends Application {
    private static int WIDTH = 1200;
    private static int HEIGHT = 800;

    @Override
    public void start(Stage stage) throws Exception {
        Planet Sun = new Planet(0,0,0,0,0,0, 50, 1.99*Math.pow(10, 30), "Sun.jpg");
        Planet earth = new Planet(100,0,0,0,0,0,20,5.97*Math.pow(10, 24), "Earth.jpg");

        Group planets = new Group();
        planets.getChildren().add(Sun);
        planets.getChildren().add(earth);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500); // Move camera back
        camera.setFarClip(1000);
        camera.setNearClip(0);

        SubScene subScene = new SubScene(planets, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        Group root = new Group();
        root.getChildren().add(subScene);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Solar System");
        stage.show();
    }
}
