package Backend.SolarSystem;

import Backend.Physics.State;
import Utils.vec;
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

/**
 * The {@code SpaceShip} class extends {@code Group} to give it additional
 * information needed to represent the SpaceShip in a javafx scene.
 * <ul>
 * <li>contains {@code State} object</li>
 * <li>contains {@code Mass} value</li>
 * <li>contains {@code Fuel} value</li>
 * <li>scales for JavaFX</li>
 * </ul>
 */
public class SpaceShip extends Group implements CelestialObject {
    private State state;
    private final String name;
    private double mass;
    private double initialFuel;
    private double currentFuel;

    public double SCALE = 1e6;

    public SpaceShip(String spaceShipName, double[] position, double[] velocity, double weight, double fuel) {
        createSpaceShip(spaceShipName);
        this.name = spaceShipName;
        this.state = new State(0, position, velocity);
        this.mass = weight;
        this.initialFuel = fuel;
        this.currentFuel = fuel;

        moveCelestialObject(position);
    }

    public String getName() {
        return this.name;
    }
    public State getState() {
        return this.state;
    }
    public double getMass() {
        return this.mass;
    }
    public double getFuel() {
        return this.currentFuel;
    }

    public void setState(State state) {
        this.state = state;
    }
    public void setMass(double weight) {
        this.mass = weight;
    }
    public void setFuel(double fuel) {
        this.currentFuel = fuel;
    }

    public double calculateEscapeVelocity(CelestialObject celestialBody){
        double[] bodyPos = celestialBody.getState().getPos();
        double[] shipPos = this.getState().getPos();
        double[] relativePos = vec.substract(bodyPos, shipPos);
        double distance = vec.magnitude(relativePos);

        //Apply the escape velocity formula: v_escape = sqrt(2*G*M/r)
        double G = 6.67430e-11; //Universal Gravitational Constant
        return Math.sqrt(2*G*this.getMass()/distance);
    }

    public double getFuelPercentage(){
        if (this.getFuel() == 0){return 0;}
        return (this.getFuel()/this.initialFuel) * 100;
    }
    public void consumeFuel(double fuel) {
        this.currentFuel -= fuel;
    }

    public boolean applyImpulse(double[] direction, double magnitude) {
        // Check if we have enough fuel
        if (this.currentFuel <= 0) {
            System.out.println("Out of fuel!");
            return false;
        }

        // Normalize the direction vector
        double[] normalizedDir = vec.normalize(direction);

        // Calculate velocity change based on impulse and mass
        double scale = magnitude / mass;
        double[] deltaV = vec.multiply(normalizedDir, scale);

        // Update velocity
        state.addVel(deltaV);

        // Consume fuel (simple model: fuel consumption proportional to impulse)
        consumeFuel(magnitude * 0.01); // Adjust the factor as needed

        return true;
    }


    private void createSpaceShip(String spaceShipName) {
        if (spaceShipName.equals("RocketShip")) {
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
            getTransforms().add(new Scale(0.2, 0.2, 0.2));
        } else if (spaceShipName.equals("XWing")) {
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
                    new Translate(0,-2,0));

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
            getTransforms().add(new Scale(0.2, 0.2, 0.2));
        }
    }

    public void moveCelestialObject(double[] newPosition) {
        setTranslateX(newPosition[0] / SCALE);
        setTranslateY(newPosition[2] / SCALE);
        setTranslateZ(newPosition[1] / SCALE);
    }

    public void setScale(double scale) {
        this.SCALE = scale;
    }

    @Override
    public boolean isSpaceship() {
        return true;
    }

    public void applyImpulse(double[] vel) {
        this.state.addVel(vel);
        // missing fuel reduction calc
    }
}