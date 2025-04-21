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

/**
 * The {@code SpaceShip} class extends {@code Group} to give it additional information needed to represent the SpaceShip in a javafx scene.
 * <ul>
 *     <li>contains {@code State} object</li>
 *     <li>contains {@code Mass} value</li>
 *     <li>contains {@code Fuel} value</li>
 *     <li>scales for JavaFX</li>
 * </ul>
 */
public class SpaceShip extends Group implements  CelestialObject {
    private State state;
    private double mass;
    private double fuel;

    private static final double SCALE = 1e4;

    public SpaceShip(double[] position, double[] velocity, double weight, double fuel) {
        createSpaceShip();
        this.state = new State(0, position, velocity);
        this.mass = weight;
        this.fuel = fuel;

        moveCelestialObject(position);
    }
    
    public State getState() {
        return this.state;
    }
    public double getMass() {
        return this.mass;
    }
    public double getFuel() {
        return this.fuel;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    public void setMass(double weight) {
        this.mass = weight;
    }
    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    private void createSpaceShip() {
        // create colours
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

        // create shapes
        Cylinder mainBody = new Cylinder(4, 20);
        mainBody.setMaterial(bodyMaterial);
        mainBody.getTransforms().addAll(
                new Translate(0, 2, 0)
        );

        Cone noseCone = new Cone(4,1,4);
        noseCone.setMaterial(noseMaterial);
        noseCone.getTransforms().addAll(
                new Translate(0, -10, 0)
        );

        Sphere window = new Sphere(2);
        window.setMaterial(windowMaterial);
        window.getTransforms().addAll(
                new Translate(0, -2, -4)
        );

        Box fin1 = new Box(0.5, 10, 4);
        fin1.setMaterial(finMaterial);
        fin1.getTransforms().addAll(
                new Translate(0, 13, 4),
                new Rotate(20, Rotate.X_AXIS)
        );

        Box fin2 = new Box(0.5, 10, 4);
        fin2.setMaterial(finMaterial);
        fin2.getTransforms().addAll(
                new Translate(0, 13, -4),
                new Rotate(-20, Rotate.X_AXIS)
        );

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
                new Rotate(20, Rotate.Z_AXIS)
        );

        Cone rocketFlames = new Cone(0.4,2,8);
        rocketFlames.setMaterial(rocketFlamesMaterial);
        rocketFlames.getTransforms().addAll(
                new Translate(0, 13, 0)
        );

        getChildren().addAll(mainBody, noseCone, window, fin1, fin2, fin3, fin4, rocketFlames);
        getTransforms().add(new Scale(0.2, 0.2, 0.2));
    }

    public void moveCelestialObject(double[] newPosition) {
        setTranslateX(newPosition[0]/SCALE);
        setTranslateY(newPosition[1]/SCALE);
        setTranslateZ(newPosition[2]/SCALE);
    }
}