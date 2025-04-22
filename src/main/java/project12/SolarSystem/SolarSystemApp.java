package project12.SolarSystem;

import Backend.SolarSystem.CelestialObject;
import Backend.SolarSystem.Planet;
import Backend.SolarSystem.SpaceShip;
import Backend.SolarSystem.SolarSystemFunctions;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Arrays;

public class SolarSystemApp extends Application {
    private final static int WIDTH = 1600;
    private final static int HEIGHT = 1000;

    private static final double SCALE = 1e4;
    private final static String csvpath = "SolarSystemValues.csv";

    private double cameraDistance = -2000;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private double[] currentFocus = new double[]{0, 0, 0};

    private final ArrayList<SpaceShip> availableSpaceShips = new ArrayList<>(Arrays.asList(
            new SpaceShip( "RocketShip", new double[]{-1.47E+08, -3E+07, 2.75E+04}, new double[]{0, 0, 0}, 50000, 0),
            new SpaceShip("RocketShip", new double[]{-1.47E+08, -3E+07, 2.75E+04}, new double[]{0, 0, 0}, 50000, 0)
    ));

    private final String[] spaceShipNames = {"RocketShip", "StarDestroyer"};

    @Override
    public void start(Stage stage) {
        showMenuScene(stage);
    }

    private void showMenuScene(Stage stage) {
        VBox menuLayout = new VBox(30);
        menuLayout.setAlignment(Pos.CENTER);

        Image backgroundImage = new Image(getClass().getResource("/images/RocketDepartingFromEarth.jpg").toString());
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setFitWidth(WIDTH * 1.2);
        backgroundImageView.setFitHeight(HEIGHT * 1.2);
        backgroundImageView.setTranslateY(-40);

        StackPane.setAlignment(backgroundImageView, Pos.CENTER);

        Label titleLabel = new Label("Solar System Explorer");
        titleLabel.setStyle(
                "-fx-text-fill: #90caf9; " +
                "-fx-font-size: 46px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 0 20px 0 0; "
        );

        VBox selectionBox = new VBox(10);
        selectionBox.setAlignment(Pos.CENTER);

        Label selectShipLabel = new Label("Select your spaceship:");
        selectShipLabel.setStyle(
                "-fx-text-fill: white; " +
                "-fx-background-color: #141e32; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 10px; " +
                "-fx-border-color: #3498db; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; "
        );
        ComboBox<String> shipSelector = new ComboBox<>();
        shipSelector.setStyle(
                "-fx-text-fill: white; " +
                "-fx-background-color: #141e32; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 10px; " +
                "-fx-border-color: #3498db; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; "
        );
        for (String name : spaceShipNames) {
            shipSelector.getItems().add(name);
        }
        shipSelector.setPromptText("Select a spaceship");
        shipSelector.getSelectionModel().selectFirst();

        selectionBox.getChildren().addAll(selectShipLabel, shipSelector);

        Button startButton = new Button("Launch Mission");
        startButton.setStyle(
                "-fx-background-color: #2980b9; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 6px; " +
                "-fx-padding: 8px 12px; "
        );
        startButton.setOnAction(e -> {
            int selectedIndex = shipSelector.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0) selectedIndex = 0;
            showSolarSystemScene(stage, selectedIndex);
        });

        menuLayout.getChildren().addAll(titleLabel, selectionBox, startButton);

        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundImageView, menuLayout);
        root.setStyle(
                "-fx-background-color: #090f1d; "
        );

        Scene menuScene = new Scene(root, 1000, 900);
        stage.setScene(menuScene);
        stage.setTitle("Solar System Explorer - Menu");
        stage.centerOnScreen();
        stage.show();
    }
    private void showSolarSystemScene(Stage stage, int spaceShipIndex) {
        ArrayList<Planet> planetList = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(csvpath);
        SpaceShip selectedShip = availableSpaceShips.get(spaceShipIndex);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(1e6);
        camera.setNearClip(0.1);
        changeCameraPos(selectedShip, camera);

        SubScene subScene = createSubScene(planetList, selectedShip);
        subScene.setCamera(camera);


        VBox planetSelector = initPlanetSelector(planetList, camera);

        Button focusSpaceShipButton = initSpaceShipSelector(selectedShip, camera);

        Button backToMenuButton = new Button("Return to Menu");
        backToMenuButton.setOnAction(e -> showMenuScene(stage));
        backToMenuButton.setStyle(
                "-fx-background-color: #34495e; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 12px;"
        );

        FlowPane controlsPane = new FlowPane(15, 15);
        controlsPane.setPadding(new Insets(12));
        controlsPane.setStyle(
                "-fx-background-color: #0a0f1e; " +
                "-fx-border-color: #1a237e; " +
                "-fx-border-width: 0 0 1px 0;"
        );
        controlsPane.setAlignment(Pos.CENTER_LEFT);
        controlsPane.getChildren().addAll(planetSelector, focusSpaceShipButton, backToMenuButton);

        Label controlsTitle = new Label("Navigation Controls");
        controlsTitle.setStyle(
                "-fx-text-fill: #90caf9; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 0 20px 0 0;"
        );
        controlsPane.getChildren().addFirst(controlsTitle);

        BorderPane root = new BorderPane();
        root.setCenter(subScene);
        root.setTop(controlsPane);

        Scene scene = new Scene(root, WIDTH, HEIGHT, false);
        initMouseControl(scene);
        initZoomControl(scene, camera);

        stage.setScene(scene);
        stage.setTitle("Solar System Explorer - " + spaceShipNames[spaceShipIndex]);
        stage.centerOnScreen();
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

    private VBox initPlanetSelector(ArrayList<Planet> planetList, PerspectiveCamera camera) {
        ComboBox<Planet> planetSelector = new ComboBox<>();
        planetSelector.getItems().addAll(planetList);
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
                changeCameraPos(selected, camera);
            }
        });

        VBox menu = new VBox(10, planetSelector);
        menu.setPadding(new Insets(10));
        planetSelector.setStyle(
                "-fx-text-fill: white; " +
                "-fx-background-color: rgba(20, 30, 50, 0.8); " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 10px; " +
                "-fx-border-color: #3498db; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px;"
        );
        return menu;
    }
    private Button initSpaceShipSelector(SpaceShip spaceShip, PerspectiveCamera camera) {
        Button focusSpaceShipButton = new Button("Focus Space Ship");
        focusSpaceShipButton.setOnAction(e -> {
            changeCameraPos(spaceShip, camera);
        });
        focusSpaceShipButton.setStyle(
                "-fx-background-color: #2980b9; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 12px;"
        );
        return focusSpaceShipButton;
    }
    private void changeCameraPos(CelestialObject celObj, PerspectiveCamera camera) {
        currentFocus = celObj.getState().getPos();
        camera.setTranslateX(currentFocus[0]/SCALE);
        camera.setTranslateY(currentFocus[1]/SCALE);
        camera.setTranslateZ(currentFocus[2]/SCALE + cameraDistance);

        rotateX.setAngle(0);
        rotateY.setAngle(0);
    }

    private void initZoomControl(Scene scene, PerspectiveCamera camera) {
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