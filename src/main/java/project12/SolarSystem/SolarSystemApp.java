package project12.SolarSystem;

import Backend.SolarSystem.Planet;
import Backend.SolarSystem.SolarSystemFunctions;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.util.ArrayList;

public class SolarSystemApp extends Application {
    private final static int WIDTH = 1500;
    private final static int HEIGHT = 1000;

    private double cameraDistance = -1200;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    @Override
    public void start(Stage stage) {
       /* Planet Sun = new Planet(0,0,0,0,0,0, 50, 1.99*Math.pow(10, 30), "Sun.jpg");
        Planet earth = new Planet(100,0,0,0,0,0,20,5.97*Math.pow(10, 24), "Earth.jpg");*/

        String path = "SolarSystemValues.csv";
        ArrayList<Planet> PlanetList = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(path); // Add all planets to a list
        Group Planets = new Group();
        for (Planet p : PlanetList) {
            Planets.getChildren().add(p);
        }
        Planets.getTransforms().addAll(rotateX, rotateY); // Add all Systems to Planet Group

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1200);   // Move camera back
        camera.setFarClip(20000);       // Maximum render limit
        camera.setNearClip(0.1);         // minimum render limit

        SubScene subScene = new SubScene(Planets, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        Group root = new Group();
        root.getChildren().add(subScene);

        Scene scene = new Scene(root, WIDTH, HEIGHT, false);
        initMouseControl(scene);
        initZoomControl(scene, camera);

        stage.setScene(scene);
        stage.setTitle("Solar System");
        stage.show();
    }

    private void initZoomControl(Scene scene, Camera camera) {
        scene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            cameraDistance += delta;
            double zoomSpeed = 4.0;
            cameraDistance = Math.max(-8000, Math.min(-100, cameraDistance + delta * zoomSpeed));
            camera.setTranslateZ(cameraDistance);
        });
    }

    private void initMouseControl(Scene scene) {
        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        scene.setOnMouseDragged(event -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });
    }
}
