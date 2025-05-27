package Backend.SolarSystem;

import Backend.Physics.State;
import com.almasb.fxgl.scene3d.Cone;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public abstract class SpaceVessel extends Group implements CelestialObject {
    private String vesselName;
    public State state;
    public double mass;
    public double SCALE = 1e6;


    public SpaceVessel(String vesselName, double[] position, double[] velocity, double mass) {
        this.vesselName = vesselName;
        createVessel(vesselName);
        this.state = new State(0, position, velocity);
        this.mass = mass;
    }


    @Override
    public String getName() {return this.vesselName;}
    @Override
    public State getState() {return this.state;}
    @Override
    public double getMass() {return this.mass;}
    @Override
    public void setState(State state) {this.state = state;}
    @Override
    public void setScale(double scale) {this.SCALE = scale;}
    @Override
    public void setMass(double mass) {this.mass = mass;}

    @Override
    public void moveCelestialObject(double[] newPosition) {
        setTranslateX(newPosition[0]/SCALE);
        setTranslateY(newPosition[2]/SCALE);
        setTranslateZ(newPosition[1]/SCALE);
    }

    public void pointVessel(double[] position){
        // implementation required
    }

    @Override
    public boolean isSpaceship() {
        return false;
    }
    @Override
    public boolean isProbe(){
        return false;
    }

    protected void createVessel(String vesselName){
     if (vesselName.equals("RocketShip")) {
        // Create colours
        PhongMaterial bodyMaterial = new PhongMaterial();
        bodyMaterial.setDiffuseColor(Color.SILVER);
        PhongMaterial noseMaterial = new PhongMaterial();
        noseMaterial.setDiffuseColor(Color.RED);

        PhongMaterial windowMaterial = new PhongMaterial();
        windowMaterial.setDiffuseColor(new Color(0.4, 0.8, 1.0, 0.5));

        PhongMaterial finMaterial = new PhongMaterial();
        finMaterial.setDiffuseColor(Color.RED);

        PhongMaterial rocketFlamesMaterial = new PhongMaterial();
        rocketFlamesMaterial.setDiffuseColor(new Color(1.0, 0.5, 0.0, 0.5));

        // Create shapes
        Cylinder mainBody = new Cylinder(4, 20);
        mainBody.setMaterial(bodyMaterial);
        mainBody.getTransforms().addAll(
                new Translate(0, 2, 0));

        Cone noseCone = new Cone(4, 1, 4);
        noseCone.setMaterial(noseMaterial);
        noseCone.getTransforms().addAll(
                new Translate(0, -10, 0));

        Sphere window = new Sphere(2);
        window.setMaterial(windowMaterial);
        window.getTransforms().addAll(
                new Translate(0, -2, -4));

        Box fin1 = new Box(0.5, 10, 4);
        fin1.setMaterial(finMaterial);
        fin1.getTransforms().addAll(
                new Translate(0, 13, 4),
                new Rotate(20, Rotate.X_AXIS));

        Box fin2 = new Box(0.5, 10, 4);
        fin2.setMaterial(finMaterial);
        fin2.getTransforms().addAll(
                new Translate(0, 13, -4),
                new Rotate(-20, Rotate.X_AXIS));

        Box fin3 = new Box(4, 10, 0.5);
        fin3.setMaterial(finMaterial);
        fin3.getTransforms().addAll(
                new Translate(4, 13, 0),
                new Rotate(-20, Rotate.Z_AXIS)

        );

        Box fin4 = new Box(4, 10, 0.5);
        fin4.setMaterial(finMaterial);
        fin4.getTransforms().addAll(
                new Translate(-4, 13, 0),
                new Rotate(20, Rotate.Z_AXIS));

        Cone rocketFlames = new Cone(0.4, 2, 8);
        rocketFlames.setMaterial(rocketFlamesMaterial);
        rocketFlames.getTransforms().addAll(
                new Translate(0, 13, 0));

        getChildren().addAll(mainBody, noseCone, window, fin1, fin2, fin3, fin4, rocketFlames);
    } else if (vesselName.equals("XWing")) {
        // Create colors
        PhongMaterial bodyMaterial = new PhongMaterial();
        bodyMaterial.setDiffuseColor(Color.LIGHTGRAY);

        PhongMaterial cockpitMaterial = new PhongMaterial();
        cockpitMaterial.setDiffuseColor(new Color(0.4, 0.8, 1.0, 0.7));

        PhongMaterial wingMaterial = new PhongMaterial();
        wingMaterial.setDiffuseColor(Color.DARKRED);

        PhongMaterial engineMaterial = new PhongMaterial();
        engineMaterial.setDiffuseColor(Color.DARKGRAY);

        PhongMaterial boosterMaterial = new PhongMaterial();
        boosterMaterial.setDiffuseColor(new Color(0.8, 0.6, 1.0, 0.6));

        PhongMaterial gunMaterial = new PhongMaterial();
        gunMaterial.setDiffuseColor(Color.DARKGRAY);

        PhongMaterial laserMaterial = new PhongMaterial();
        laserMaterial.setDiffuseColor(Color.RED);

        // Create shapes
        Cone fuselage = new Cone(4, 2, 25);
        fuselage.setMaterial(bodyMaterial);
        fuselage.getTransforms().addAll(
                new Translate(0, 0, 0.1),
                new Rotate(90, Rotate.X_AXIS));

        Sphere cockpit = new Sphere(2.5);
        cockpit.setMaterial(cockpitMaterial);
        cockpit.getTransforms().addAll(
                new Translate(0, -2, 0));

        Box wing1 = new Box(20, 1, 5);
        wing1.setMaterial(wingMaterial);
        wing1.getTransforms().addAll(
                new Translate(10, 2, 10),
                new Rotate(15, Rotate.Z_AXIS));

        Box wing2 = new Box(20, 1, 5);
        wing2.setMaterial(wingMaterial);
        wing2.getTransforms().addAll(
                new Translate(-10, 2, 10),
                new Rotate(-15, Rotate.Z_AXIS));

        Box wing3 = new Box(20, 1, 5);
        wing3.setMaterial(wingMaterial);
        wing3.getTransforms().addAll(
                new Translate(-10, -2, 10),
                new Rotate(15, Rotate.Z_AXIS));

        Box wing4 = new Box(20, 1, 5);
        wing4.setMaterial(wingMaterial);
        wing4.getTransforms().addAll(
                new Translate(10, -2, 10),
                new Rotate(-15, Rotate.Z_AXIS));

        Cylinder engine1 = new Cylinder(2, 8);
        engine1.setMaterial(engineMaterial);
        engine1.getTransforms().addAll(
                new Translate(5, -2, 11),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder engine2 = new Cylinder(2, 8);
        engine2.setMaterial(engineMaterial);
        engine2.getTransforms().addAll(
                new Translate(-5, -2, 11),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder engine3 = new Cylinder(2, 8);
        engine3.setMaterial(engineMaterial);
        engine3.getTransforms().addAll(
                new Translate(5, 2, 11),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder engine4 = new Cylinder(2, 8);
        engine4.setMaterial(engineMaterial);
        engine4.getTransforms().addAll(
                new Translate(-5, 2, 11),
                new Rotate(90, Rotate.X_AXIS));

        Cone booster1 = new Cone(1.6, 0.2, 18);
        booster1.setMaterial(boosterMaterial);
        booster1.getTransforms().addAll(
                new Translate(5, -2, 15.7),
                new Rotate(-90, Rotate.X_AXIS));

        Cone booster2 = new Cone(1.6, 0.2, 18);
        booster2.setMaterial(boosterMaterial);
        booster2.getTransforms().addAll(
                new Translate(-5, -2, 15.7),
                new Rotate(-90, Rotate.X_AXIS));

        Cone booster3 = new Cone(1.6, 0.2, 18);
        booster3.setMaterial(boosterMaterial);
        booster3.getTransforms().addAll(
                new Translate(5, 2, 15.7),
                new Rotate(-90, Rotate.X_AXIS));

        Cone booster4 = new Cone(1.6, 0.2, 18);
        booster4.setMaterial(boosterMaterial);
        booster4.getTransforms().addAll(
                new Translate(-5, 2, 15.7),
                new Rotate(-90, Rotate.X_AXIS));

        Cylinder gun1 = new Cylinder(0.8, 7);
        gun1.setMaterial(gunMaterial);
        gun1.getTransforms().addAll(
                new Translate(20, 4.5, 9.1),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder gun2 = new Cylinder(0.8, 7);
        gun2.setMaterial(gunMaterial);
        gun2.getTransforms().addAll(
                new Translate(-20, 4.5, 9.1),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder gun3 = new Cylinder(0.8, 7);
        gun3.setMaterial(gunMaterial);
        gun3.getTransforms().addAll(
                new Translate(-20, -4.5, 9.1),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder gun4 = new Cylinder(0.8, 7);
        gun4.setMaterial(gunMaterial);
        gun4.getTransforms().addAll(
                new Translate(20, -4.5, 9.1),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder laser1 = new Cylinder(0.4, 10);
        laser1.setMaterial(laserMaterial);
        laser1.getTransforms().addAll(
                new Translate(20, 4.5, 3),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder laser2 = new Cylinder(0.4, 10);
        laser2.setMaterial(laserMaterial);
        laser2.getTransforms().addAll(
                new Translate(-20, 4.5, 3),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder laser3 = new Cylinder(0.4, 10);
        laser3.setMaterial(laserMaterial);
        laser3.getTransforms().addAll(
                new Translate(-20, -4.5, 3),
                new Rotate(90, Rotate.X_AXIS));

        Cylinder laser4 = new Cylinder(0.4, 10);
        laser4.setMaterial(laserMaterial);
        laser4.getTransforms().addAll(
                new Translate(20, -4.5, 3),
                new Rotate(90, Rotate.X_AXIS));

        getChildren().addAll(fuselage, cockpit,
                wing1, wing2, wing3, wing4,
                engine1, engine2, engine3, engine4,
                booster1, booster2, booster3, booster4,
                gun1, gun2, gun3, gun4,
                laser1, laser2, laser3, laser4);
    }
    getTransforms().add(new Scale(0.01, 0.01, 0.01));
}

}
