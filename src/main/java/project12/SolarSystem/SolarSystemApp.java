package project12.SolarSystem;

import Backend.SolarSystem.CelestialObject;
import Backend.SolarSystem.Planet;
import Backend.SolarSystem.SpaceShip;
import Backend.SolarSystem.SolarSystemFunctions;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.util.ArrayList;

public class SolarSystemApp extends Application {
    private final static int WIDTH = 1600;
    private final static int HEIGHT = 1000;

    private static final double SCALE = 1e4;
    private final static String csvpath = "SolarSystemValues.csv";

    private static final PerspectiveCamera camera = new PerspectiveCamera(true);
    private double cameraDistance = -2000;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private double[] currentFocus = new double[]{0, 0, 0};

    @Override
    public void start(Stage stage) {
        ArrayList<Planet> PlanetList = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(csvpath);
        SpaceShip spaceShip = new SpaceShip(new double[]{-1.47E+08, -3E+07, 2.75E+04}, new double[]{0, 0, 0}, 50000, 0);

        camera.setFarClip(1e6);
        camera.setNearClip(0.1);

        SubScene subScene = createSubScene(PlanetList, spaceShip);
        subScene.setCamera(camera);

        VBox planetSelector = initPlanetSelector(PlanetList);
        Button focusSpaceShipButton = initSpaceShipSelector(spaceShip);
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(planetSelector, focusSpaceShipButton);

        BorderPane root = new BorderPane();
        root.setCenter(subScene);
        root.setTop(flowPane);

        Scene scene = new Scene(root, WIDTH, HEIGHT, false);
        initMouseControl(scene);
        initZoomControl(scene);

        stage.setScene(scene);
        stage.setTitle("Solar System");
        stage.show();
    }

    private SubScene createSubScene(ArrayList<Planet> PlanetList, SpaceShip spaceShip) {
        Group SolarSystem = new Group();
        for (Planet p : PlanetList) {
            SolarSystem.getChildren().add(p);
        }
        SolarSystem.getTransforms().addAll(rotateX, rotateY);
        SolarSystem.getChildren().add(spaceShip);

        SubScene subScene = new SubScene(SolarSystem, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        return subScene;
    }

    private VBox initPlanetSelector(ArrayList<Planet> PlanetList) {
        ComboBox<Planet> planetSelector = new ComboBox<>();
        planetSelector.getItems().addAll(PlanetList);
        planetSelector.setPromptText("Select a planet");

        planetSelector.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Planet item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        planetSelector.setButtonCell(new ListCell<>() {
            protected void updateItem(Planet item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        planetSelector.setOnAction(e -> {
            Planet selected = planetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                changeCameraPos(selected);
            }
        });

        VBox menu = new VBox(10, planetSelector);
        menu.setPadding(new Insets(10));
        return menu;
    }
    private Button initSpaceShipSelector(SpaceShip spaceShip) {
        Button focusSpaceShipButton = new Button("Focus Space Ship");
        focusSpaceShipButton.setOnAction(e -> {
            changeCameraPos(spaceShip);
        });
        return focusSpaceShipButton;
    }
    private void changeCameraPos(CelestialObject celObj) {
        currentFocus = celObj.getState().getPos();
        camera.setTranslateX(currentFocus[0]/SCALE);
        camera.setTranslateY(currentFocus[1]/SCALE);
        camera.setTranslateZ(currentFocus[2]/SCALE + cameraDistance);

        rotateX.setAngle(0);
        rotateY.setAngle(0);
    }

    private void initZoomControl(Scene scene) {
        scene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            double zoomSpeed = 4.0;
            cameraDistance += delta * zoomSpeed;
            cameraDistance = Math.max(-20000, Math.min(-100, cameraDistance));
            camera.setTranslateX(currentFocus[0]/SCALE);
            camera.setTranslateY(currentFocus[1]/SCALE);
            camera.setTranslateZ(currentFocus[2]/SCALE + cameraDistance);
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
            double angleX = anchorAngleX - (anchorY - event.getSceneY());
            double angleY = anchorAngleY + (anchorX - event.getSceneX());

            rotateX.setAngle(angleX);
            rotateY.setAngle(angleY);

            rotateX.setPivotX(currentFocus[0]/SCALE);
            rotateX.setPivotY(currentFocus[1]/SCALE);
            rotateX.setPivotZ(currentFocus[2]/SCALE);

            rotateY.setPivotX(currentFocus[0]/SCALE);
            rotateY.setPivotY(currentFocus[1]/SCALE);
            rotateY.setPivotZ(currentFocus[2]/SCALE);
        });
    }
}
