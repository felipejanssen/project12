package project12.SolarSystem;

import Backend.SolarSystem.SolarSystemFunctions;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SolarSystemApp extends Application {
    private final static int WIDTH = 1500;
    private final static int HEIGHT = 1000;

    @Override
    public void start(Stage stage) {
       /* Planet Sun = new Planet(0,0,0,0,0,0, 50, 1.99*Math.pow(10, 30), "Sun.jpg");
        Planet earth = new Planet(100,0,0,0,0,0,20,5.97*Math.pow(10, 24), "Earth.jpg");*/

        String path = "SolarSystemValues.csv";
        Group PlanetarySystemGroup = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(path);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1200);   // Move camera back
        camera.setFarClip(4000);       // Maximum render limit
        camera.setNearClip(0);         // minimum render limit

        SubScene subScene = new SubScene(PlanetarySystemGroup, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
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
