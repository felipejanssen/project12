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
import javafx.scene.transform.Translate;

public class SpaceShip extends Group implements  CelestialObject {
    private State state;
    private double weight;
    private double fuel;

    private static final double xScale = 1e6;
    private static final double yScale = 1e6;
    private static final double zScale = 1e6;

    public SpaceShip(double[] position, double[] velocity, double weight, double fuel) {
        createRocketShip();
        this.state = new State(0, position, velocity);
        this.weight = weight;
        this.fuel = fuel;

        moveCelestialObject(position);
    }
    
    public State getState() {
        return this.state;
    }
    public double getMass() {
        return this.weight;
    }
    public double getFuel() {
        return this.fuel;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    public void setMass(double weight) {
        this.weight = weight;
    }
    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    private void createRocketShip() {
        // create colours
        PhongMaterial bodyMaterial = new PhongMaterial();
        bodyMaterial.setDiffuseColor(Color.SILVER);

        PhongMaterial noseMaterial = new PhongMaterial();
        noseMaterial.setDiffuseColor(Color.RED);

        PhongMaterial windowMaterial = new PhongMaterial();
        windowMaterial.setDiffuseColor(Color.SKYBLUE);

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
                new Translate(0, -2, 4)
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
    }

    public void moveCelestialObject(double[] newPosition) {
        setTranslateX(newPosition[0] / xScale);
        setTranslateY(newPosition[1] / yScale);
        setTranslateZ(newPosition[2] / zScale);
    }
}