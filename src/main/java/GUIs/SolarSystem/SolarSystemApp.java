package GUIs.SolarSystem;

import Backend.SolarSystem.*;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SolarSystemApp extends Application {
    private final static int WIDTH = 1600;
    private final static int HEIGHT = 1000;

    private double SCALE = 1e6;
    private double animationSpeed = 1;
    LocalDate startDate = LocalDate.of(2025, 4, 1);
    private SpaceShip ship;

    private AnimationTimer animationTimer;
    private final static String csvpath = "SolarSystemValues.csv";
    private final String basicButtonStyle = "-fx-text-fill: white;" +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5px; " +
            "-fx-padding: 10px; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 5px; ";
    private final String basicTextStyle = "-fx-text-fill: #90caf9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px; ";
    private double cameraDistance = -2000;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private double[] currentFocus = new double[] { 0, 0, 0 };
    private CelestialObject currentFocusObject = null;

    private double[] shipLocation = new double[] { -146936800, -29765470, 25289 };
    private final int trialSphereCap = 1000;
    private final Queue<Sphere> trailSpheres = new LinkedList<>();

    private final ArrayList<SpaceShip> availableSpaceShips = new ArrayList<>(Arrays.asList(
            new SpaceShip("RocketShip", shipLocation, new double[] { 60, -32.8, -6.4 }, 50000,
                    Double.MAX_VALUE),
            new SpaceShip("XWing", shipLocation, new double[] { 60, -32.8, -6.4 }, 50000,
                    Double.MAX_VALUE)));

    private final String[] spaceShipNames = { "Rocket Ship", "X-Wing" };

    @Override
    public void start(Stage stage) {
        showMenuScene(stage);
    }

    private void startAnimation(ArrayList<CelestialObject> bodies, SubScene subScene,
            PerspectiveCamera camera, VBox velocityLabels ,Label dateLabel) {

        animationTimer = new AnimationTimer() {
            // ── physics state ──
            double simTime = 0.0;
            final double physicsDt = 600.0; // 10 minutes per RK4 step
            final double endTime = 365 * 24 * 3600;
            final SolarSystemSimulator sim = new SolarSystemSimulator(bodies, simTime, physicsDt, endTime, new ArrayList<>());

            @Override
            public void handle(long now) {
                Group root3D = (Group) subScene.getRoot();

                // 1) advance N small physics steps
                for (int i = 0; i < animationSpeed; i++) {
                    sim.simulate(simTime);
                    simTime += physicsDt;
                }
                boolean makeTrail = (long) (simTime / physicsDt) % 25 == 0;

                // 2) draw each body and optional trail
                for (CelestialObject body : sim.getBodies()) {
                    double[] relPos = vec.substract(body.getState().getPos(), new double[] { 0, 0, 0 });

                    if (makeTrail) {
                        Sphere trail;
                        if (body.isSpaceship()) {
                            trail = new Sphere(0.01);
                            ship.pointVessel(shipLocation);
                            shipLocation = ship.getState().getPos();

                        } else {
                            trail = new Sphere(0.3);
                        }
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

                    body.moveCelestialObject(body.getState().getPos());

                    if (!body.isSpaceship()) {
                        ((Planet) body).spinPlanet();
                    }
                }

                // 4) update camera to track current focus
                changeCameraPos(currentFocusObject, camera);

                updateVelocityLabels(velocityLabels);

                long daysPassed = (long)(simTime / 86400);
                LocalDate currentDate = startDate.plusDays(daysPassed);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH);
                String formattedDate = currentDate.format(formatter);
                dateLabel.setText(formattedDate);
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
                        "-fx-padding: 0 20px 0 0; ");

        VBox selectionBox = new VBox(10);
        selectionBox.setAlignment(Pos.CENTER);

        Label selectShipLabel = new Label("Select your spaceship:");
        selectShipLabel.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #141e32; ");
        ComboBox<String> shipSelector = new ComboBox<>();
        shipSelector.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #141e32; ");
        for (String name : spaceShipNames) {
            shipSelector.getItems().add(name);
        }
        shipSelector.setPromptText("Select a spaceship");
        shipSelector.getSelectionModel().selectFirst();

        selectionBox.getChildren().addAll(selectShipLabel, shipSelector);

        Button startButton = new Button("Launch Mission");
        startButton.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #2980b9; ");
        startButton.setOnAction(e -> {
            int selectedIndex = shipSelector.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0)
                selectedIndex = 0;
            showSolarSystemScene(stage, selectedIndex);
        });

        menuLayout.getChildren().addAll(titleLabel, selectionBox, startButton);

        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundImageView, menuLayout);
        root.setStyle(
                "-fx-background-color: #090f1d; ");

        Scene menuScene = new Scene(root, 1000, 900);
        stage.setScene(menuScene);
        stage.setTitle("Solar System Explorer - Menu");
        stage.centerOnScreen();
        stage.show();
    }
    private void showSolarSystemScene(Stage stage, int spaceShipIndex) {
        ArrayList<CelestialObject> planetList = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(csvpath);
        SpaceShip selectedShip = availableSpaceShips.get(spaceShipIndex);
        ArrayList<CelestialObject> bodies = new ArrayList<>(planetList);
        this.ship = selectedShip;
        bodies.add(ship);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(1e8);
        camera.setNearClip(0.1);
        changeCameraPos(selectedShip, camera);

        SubScene subScene = createSubScene(planetList, selectedShip);
        subScene.setCamera(camera);

        VBox planetSelector = initPlanetSelector(planetList, camera);
        Button focusSpaceShipButton = initSpaceShipSelector(camera);
        ToggleButton playPauseButton = initPlayPauseButton();
        HBox scaleControl = initScaleControl(SCALE, bodies, subScene, camera);
        HBox speedControl = initAnimationSpeedControl(animationSpeed);
        VBox velocityLabels = initVelocityLabels();
        Label dateLabel = new Label("April 1");
        dateLabel.setPrefWidth(120);
        dateLabel.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #283d5c; ");
        Button backToMenuButton = initBackToMenuButton(stage);

        HBox leftControls = new HBox(15, planetSelector, focusSpaceShipButton, playPauseButton, scaleControl,
                speedControl);
        leftControls.setAlignment(Pos.CENTER_LEFT);

        HBox rightControls = new HBox(15, velocityLabels, dateLabel, backToMenuButton);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        Label controlsTitle = new Label("Navigation Controls");
        controlsTitle.setStyle(
                basicTextStyle);

        BorderPane controlsPane = new BorderPane();
        controlsPane.setLeft(new VBox(controlsTitle, leftControls));
        controlsPane.setRight(rightControls);
        controlsPane.setPadding(new Insets(12));
        controlsPane.setStyle(
                "-fx-background-color: #0a0f1e; " +
                        "-fx-border-color: #1a237e; " +
                        "-fx-border-width: 0 0 1px 0;");

        BorderPane root = new BorderPane();
        root.setCenter(subScene);
        root.setTop(controlsPane);

        Scene scene = new Scene(root, WIDTH, HEIGHT, false);
        initMouseControl(scene);
        initZoomControl(scene, camera);
        changeCameraPos(selectedShip, camera);
        SCALE = 1e6;
        animationSpeed = 3;

        stage.setScene(scene);
        stage.setTitle("Solar System Explorer - " + spaceShipNames[spaceShipIndex]);
        stage.centerOnScreen();
        stage.show();
        startAnimation(bodies, subScene, camera, velocityLabels, dateLabel);
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
                        "-fx-background-color: #141e32; ");

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
    private Button initSpaceShipSelector(PerspectiveCamera camera) {
        Button focusSpaceShipButton = new Button("Focus Space Ship");
        focusSpaceShipButton.setOnAction(e -> {
            changeCameraPos(ship, camera);
        });
        focusSpaceShipButton.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #1c608c; ");
        return focusSpaceShipButton;
    }
    private ToggleButton initPlayPauseButton() {
        ToggleButton playPauseButton = new ToggleButton("Play");
        playPauseButton.setPrefWidth(80);
        playPauseButton.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #3682ca; ");
        playPauseButton.setOnAction(e -> {
            if (playPauseButton.isSelected()) {
                playPauseButton.setText("Pause");
                animationTimer.start();
                playPauseButton.setStyle(
                        basicButtonStyle +
                                "-fx-background-color: #165286; ");
            } else {
                playPauseButton.setText("Play");
                animationTimer.stop();
                playPauseButton.setStyle(
                        basicButtonStyle +
                                "-fx-background-color: #3682ca; ");
            }
        });
        return playPauseButton;
    }
    private HBox initScaleControl(double initialScale, ArrayList<CelestialObject> bodies, SubScene subScene,
            PerspectiveCamera camera) {
        Label scaleLabel = new Label("Scale: ");
        scaleLabel.setStyle(
                basicTextStyle);
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
                basicTextStyle);
        Slider speedSlider = new Slider(1, 50, initialSpeed);
        speedSlider.setPrefWidth(200);
        speedSlider.setStyle("-fx-control-inner-background: #263547;");

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationSpeed = newVal.doubleValue();
        });

        HBox speedControl = new HBox(10, speedLabel, speedSlider);
        speedControl.setAlignment(Pos.CENTER);

        return speedControl;
    }
    private VBox initVelocityLabels() {
        Label xVelLabel = new Label(String.format("X: %.5f km/s", ship.getState().getVel()[0]));
        xVelLabel.setStyle(basicTextStyle);
        Label yVelLabel = new Label(String.format("Y: %.5f km/s", ship.getState().getVel()[1]));
        yVelLabel.setStyle(basicTextStyle);
        Label zVelLabel = new Label(String.format("Z: %.5f km/s", ship.getState().getVel()[2]));
        zVelLabel.setStyle(basicTextStyle);

        VBox velocityLabels = new VBox(0.1, xVelLabel, yVelLabel, zVelLabel);
        velocityLabels.setPrefWidth(180);
        velocityLabels.setStyle(
                basicButtonStyle +
                        "-fx-background-color: #263547; "
        );
        return velocityLabels;
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
                        "-fx-padding: 8px 12px;");
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
            cameraDistance = Math.max(-20000, Math.min(-0.1, cameraDistance));

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
        camera.setTranslateX(currentFocus[0] / SCALE);
        camera.setTranslateY(currentFocus[2] / SCALE);
        camera.setTranslateZ(currentFocus[1] / SCALE + cameraDistance);
    }
    private void remainFocus() {
        rotateX.setPivotX(currentFocus[0] / SCALE);
        rotateX.setPivotY(currentFocus[2] / SCALE);
        rotateX.setPivotZ(currentFocus[1] / SCALE);

        rotateY.setPivotX(currentFocus[0] / SCALE);
        rotateY.setPivotY(currentFocus[2] / SCALE);
        rotateY.setPivotZ(currentFocus[1] / SCALE);
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
    private void updateVelocityLabels(VBox velocityBox) {
        double[] velocity = ship.getState().getVel();
        ((Label) velocityBox.getChildren().get(0)).setText(String.format("X: %.5f km/s", velocity[0]));
        ((Label) velocityBox.getChildren().get(1)).setText(String.format("Y: %.5f km/s", velocity[1]));
        ((Label) velocityBox.getChildren().get(2)).setText(String.format("Z: %.5f km/s", velocity[2]));

    }
    private void clearTrails(SubScene subScene) {
        Group root = (Group) subScene.getRoot();
        root.getChildren().removeIf(node -> node instanceof Sphere);
    }
}