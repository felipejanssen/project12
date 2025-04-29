package project12.SolarSystem;

import Backend.Physics.SolarSystemEngine;
import Backend.SolarSystem.CelestialObject;
import Backend.SolarSystem.Planet;
import Backend.SolarSystem.SpaceShip;
import Backend.SolarSystem.SolarSystemFunctions;
import Utils.vec;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SolarSystemApp extends Application {
    private final static int WIDTH = 1600;
    private final static int HEIGHT = 1000;

    private double SCALE = 1e6;
    private double animationSpeed = 50;

    private AnimationTimer animationTimer;
    private final static String csvpath = "SolarSystemValues.csv";
    private final String basicButtonStyle =
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5px; " +
            "-fx-padding: 10px; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 5px; ";
    private final String basicTextStyle =
            "-fx-text-fill: #90caf9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px; ";
    private double cameraDistance = -2000;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private double[] currentFocus = new double[]{0, 0, 0};
    private CelestialObject currentFocusObject = null;

    private final int trialSphereCap = 1000;
    private final Queue<Sphere> trailSpheres = new LinkedList<>();

    private final ArrayList<SpaceShip> availableSpaceShips = new ArrayList<>(Arrays.asList(
            new SpaceShip( "RocketShip", new double[]{-1.47E+08, -3.1E+07, 2.75E+04}, new double[]{0, 0, 0}, 50000, 0),
            new SpaceShip("XWing", new double[]{-1.47E+08, -3.1E+07, 2.75E+04}, new double[]{0, 0, 0}, 50000, 0)
    ));

    private final String[] spaceShipNames = {"Rocket Ship", "X-Wing"};

    @Override
    public void start(Stage stage) {
        showMenuScene(stage);
    }

    private void startAnimation(ArrayList<CelestialObject> planets, SpaceShip spaceShip, SubScene subScene, PerspectiveCamera camera) {
        ArrayList<CelestialObject> allBodies = new ArrayList<>(planets);

        SolarSystemEngine engine = new SolarSystemEngine(allBodies);

        animationTimer = new AnimationTimer() {
            // ── physics state ──
            private double simTime    = 0.0;
            private final double physicsDt = 600.0;  // 10 minutes per RK4 step

            @Override
            public void handle(long now) {
                Group root3D = (Group) subScene.getRoot();

                // 1) advance N small physics steps
                for (int i = 0; i < animationSpeed; i++) {
                    engine.evolve(simTime, physicsDt);
                    simTime += physicsDt;
                }

                // 2) draw each body and optional trail
                for (CelestialObject body : allBodies) {
                    // compute Sun-relative coordinates
                    double[] relPos = vec.substract(body.getState().getPos(), new double[]{0,0,0});

                    // every 3 steps (≈30 min) drop a trail dot
                    if (((int)(simTime / physicsDt) % 3) == 0) {
                        Sphere trail = new Sphere(0.3);
                        trail.setTranslateX(relPos[0] / SCALE);
                        trail.setTranslateY(relPos[2] / SCALE);
                        trail.setTranslateZ(relPos[1] / SCALE);

                        root3D.getChildren().add(trail);
                        trailSpheres.offer(trail);

                        if (trailSpheres.size() > trialSphereCap) {
                            Sphere oldestTrail = trailSpheres.poll();
                            root3D.getChildren().remove(oldestTrail);
                        }
                    }

                    // move the visual node
                    body.moveCelestialObject(relPos);

                    ((Planet) body).spinPlanet();
                }

                // 4) update camera to track current focus
                changeCameraPos(currentFocusObject, camera);
            }
        };
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
                basicButtonStyle +
                "-fx-background-color: #141e32; "
        );
        ComboBox<String> shipSelector = new ComboBox<>();
        shipSelector.setStyle(
                basicButtonStyle +
                "-fx-background-color: #141e32; "
        );
        for (String name : spaceShipNames) {
            shipSelector.getItems().add(name);
        }
        shipSelector.setPromptText("Select a spaceship");
        shipSelector.getSelectionModel().selectFirst();

        selectionBox.getChildren().addAll(selectShipLabel, shipSelector);

        Button startButton = new Button("Launch Mission");
        startButton.setStyle(
                basicButtonStyle +
                "-fx-background-color: #2980b9; "
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
        ArrayList<CelestialObject> planetList = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(csvpath);
        SpaceShip selectedShip = availableSpaceShips.get(spaceShipIndex);
        ArrayList<CelestialObject> bodies = new ArrayList<>(planetList); bodies.add(selectedShip);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(1e8);
        camera.setNearClip(0.1);
        changeCameraPos(selectedShip, camera);

        SubScene subScene = createSubScene(planetList, selectedShip);
        subScene.setCamera(camera);

        VBox planetSelector = initPlanetSelector(planetList, camera);
        Button focusSpaceShipButton = initSpaceShipSelector(selectedShip, camera);
        ToggleButton playPauseButton = initPlayPauseButton();
        HBox scaleControl = initScaleControl(SCALE, bodies, subScene, camera);
        HBox speedControl = initAnimationSpeedControl(animationSpeed);
        Button backToMenuButton = initBackToMenuButton(stage);

        HBox leftControls = new HBox(15, planetSelector, focusSpaceShipButton, playPauseButton, scaleControl, speedControl);
        leftControls.setAlignment(Pos.CENTER_LEFT);

        HBox rightControls = new HBox(backToMenuButton);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        Label controlsTitle = new Label("Navigation Controls");
        controlsTitle.setStyle(
                basicTextStyle
        );

        BorderPane controlsPane = new BorderPane();
        controlsPane.setLeft(new VBox(controlsTitle, leftControls));
        controlsPane.setRight(rightControls);
        controlsPane.setPadding(new Insets(12));
        controlsPane.setStyle(
                "-fx-background-color: #0a0f1e; " +
                "-fx-border-color: #1a237e; " +
                "-fx-border-width: 0 0 1px 0;"
        );

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
        startAnimation(planetList, selectedShip, subScene, camera);
    }
    private SubScene createSubScene(ArrayList<CelestialObject> planetList, SpaceShip spaceShip) {
        Group SolarSystem = new Group();
        for (CelestialObject p : planetList) {
            SolarSystem.getChildren().add((Planet) p);
        }
        SolarSystem.getTransforms().addAll(rotateX, rotateY);
        SolarSystem.getChildren().add(spaceShip);

        SubScene subScene = new SubScene(SolarSystem, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        return subScene;
    }

    private VBox initPlanetSelector(ArrayList<CelestialObject> planetList, PerspectiveCamera camera) {
        ComboBox<Planet> planetSelector = new ComboBox<>();
        for (CelestialObject p : planetList) {
            planetSelector.getItems().add((Planet) p);
        }
        planetSelector.setPromptText("Select a planet");
        planetSelector.setStyle(
                basicButtonStyle +
                "-fx-background-color: #141e32; "
        );

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
            CelestialObject selected = planetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                changeCameraPos(selected, camera);
            }
        });

        VBox menu = new VBox(10, planetSelector);
        menu.setPadding(new Insets(10));
        return menu;
    }
    private Button initSpaceShipSelector(SpaceShip spaceShip, PerspectiveCamera camera) {
        Button focusSpaceShipButton = new Button("Focus Space Ship");
        focusSpaceShipButton.setOnAction(e -> {
            changeCameraPos(spaceShip, camera);
        });
        focusSpaceShipButton.setStyle(
                basicButtonStyle +
                "-fx-background-color: #1c608c; "
        );
        return focusSpaceShipButton;
    }
    private ToggleButton initPlayPauseButton() {
        ToggleButton playPauseButton = new ToggleButton("Play");
        playPauseButton.setPrefWidth(80);
        playPauseButton.setStyle(
                basicButtonStyle +
                "-fx-background-color: #3682ca; "
        );
        playPauseButton.setOnAction(e -> {
            if (playPauseButton.isSelected()) {
                playPauseButton.setText("Pause");
                animationTimer.start();
                playPauseButton.setStyle(
                        basicButtonStyle +
                        "-fx-background-color: #165286; "
                );
            } else {
                playPauseButton.setText("Play");
                animationTimer.stop();
                playPauseButton.setStyle(
                        basicButtonStyle +
                        "-fx-background-color: #3682ca; "
                );
            }
        });
        return playPauseButton;
    }
    private HBox initScaleControl(double initialScale, ArrayList<CelestialObject> bodies, SubScene subScene, PerspectiveCamera camera) {
        Label scaleLabel = new Label("Scale: ");
        scaleLabel.setStyle(
                basicTextStyle
        );
        Slider scaleSlider = new Slider(1e4, 1e6, initialScale);
        scaleSlider.setPrefWidth(200);
        scaleSlider.setStyle("-fx-control-inner-background: #263547;");

        scaleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            setScale(newVal.doubleValue(), bodies, subScene);
            changeCameraPos(currentFocusObject, camera);
        });

        HBox scaleControl = new HBox(10, scaleLabel, scaleSlider);
        scaleControl.setAlignment(Pos.CENTER);

        return scaleControl;
    }
    private HBox initAnimationSpeedControl(double initialSpeed) {
        Label speedLabel = new Label("Animation Speed: ");
        speedLabel.setStyle(
                basicTextStyle
        );
        Slider speedSlider = new Slider(1, 100, initialSpeed);
        speedSlider.setPrefWidth(200);
        speedSlider.setStyle("-fx-control-inner-background: #263547;");

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationSpeed = newVal.doubleValue();
        });

        HBox speedControl = new HBox(10, speedLabel, speedSlider);
        speedControl.setAlignment(Pos.CENTER);

        return speedControl;
    }
    private Button initBackToMenuButton(Stage stage) {
        Button backToMenuButton = new Button("Return to Menu");
        backToMenuButton.setOnAction(e -> {
            animationTimer.stop();
            showMenuScene(stage);
        });

        backToMenuButton.setStyle(
                "-fx-background-color: #34495e; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 12px;"
        );
        backToMenuButton.setAlignment(Pos.TOP_RIGHT);
        return backToMenuButton;
    }

    private void initZoomControl(Scene scene, PerspectiveCamera camera) {
        scene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            double zoomFactor = 1.3;
            if (delta < 0) {
                cameraDistance *= zoomFactor;
            } else {
                cameraDistance /= zoomFactor;
            }
            cameraDistance = Math.max(-20000, Math.min(-2, cameraDistance));

            camera.setTranslateX(currentFocus[0] / SCALE);
            camera.setTranslateY(currentFocus[2] / SCALE);
            camera.setTranslateZ(currentFocus[1] / SCALE + cameraDistance);
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

           remainFocus();
        });
    }
    private void changeCameraPos(CelestialObject celObj, PerspectiveCamera camera) {
        currentFocus = celObj.getState().getPos();
        currentFocusObject = celObj;
        remainFocus();
        camera.setTranslateX(currentFocus[0]/SCALE);
        camera.setTranslateY(currentFocus[2]/SCALE);
        camera.setTranslateZ(currentFocus[1]/SCALE + cameraDistance);
    }
    private void remainFocus() {
        rotateX.setPivotX(currentFocus[0]/SCALE);
        rotateX.setPivotY(currentFocus[2]/SCALE);
        rotateX.setPivotZ(currentFocus[1]/SCALE);

        rotateY.setPivotX(currentFocus[0]/SCALE);
        rotateY.setPivotY(currentFocus[2]/SCALE);
        rotateY.setPivotZ(currentFocus[1]/SCALE);
    }
    private void setScale(double scale, ArrayList<CelestialObject> bodies, SubScene subScene) {
        this.SCALE = scale;
        for (CelestialObject obj : bodies) {
            obj.setScale(scale);
            double[] pos = obj.getState().getPos();
            obj.moveCelestialObject(pos);
        }
        clearTrails(subScene);
    }
    private void clearTrails(SubScene subScene) {
        Group root = (Group) subScene.getRoot();
        root.getChildren().removeIf(node -> node instanceof Sphere);
    }
}