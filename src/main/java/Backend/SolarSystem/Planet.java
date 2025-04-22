package Backend.SolarSystem;

import Backend.Physics.State;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * The {@code Planet} class extends {@code Group} to give it additional information needed to represent a planet in a javafx scene.
 * <ul>
 *     <li>contains {@code State} object</li>
 *     <li>contains {@code Mass} value</li>
 *     <li>applies {@code Texture}, {@code Tilt} and {@code Ring}</li>
 *     <li>scales for {@code JavaFX}</li>
 * </ul>
 */
public class Planet extends Group implements CelestialObject {
    private final String name;
    private State state;
    private double mass;
    private final double radius;
    private final Sphere sphere;

    private static final double SCALE = 1e6;

    public Planet(String name, double[] position, double[] velocity, double mass, int ringType) {
        this.name = name;
        this.mass = mass;
        this.radius = SolarSystemFunctions.estimateRadiusFromMass(mass);
        this.sphere = new Sphere(radius);
        this.state = new State(0, position, velocity);

        if(ringType != 0)
            setRing(ringType);
        setTexture(name + ".jpg");
        moveCelestialObject(position);
        addRandomTilt();

        getChildren().add(sphere);
    }
    public String getName() {
        return name;
    }
    public State getState() {
        return state;
    }
    public double getMass() {
        return mass;
    }

    public void setState(State state) {
        this.state = state;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }

    private void setTexture(String texturePath) {

        try {
            String pathString  = "/images/PlanetTextures/" + texturePath;
            Image texture = new Image(getClass().getResource(pathString).toString());

            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(texture);
            sphere.setMaterial(material);

        } catch (Exception e) {
            System.err.println("Error loading planet texture: " + e.getMessage());
            sphere.setMaterial(new PhongMaterial(Color.GRAY));
        }
    }
    private void setRing(int ringType) {
        Cylinder ring = new Cylinder(radius * 1.7, 0.1);

        PhongMaterial ringMaterial = new PhongMaterial();

        if (ringType == 2) {
            ringMaterial.setDiffuseColor(Color.rgb(135, 206, 235, 0.3));
            ring.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
        }
        else if (ringType == 1) {
            ringMaterial.setDiffuseColor(Color.rgb(210, 180, 120, 0.3));
            ring.getTransforms().add(new Rotate(0, Rotate.X_AXIS));
        }

        ring.setMaterial(ringMaterial);
        getChildren().add(ring);
    }
    private void addRandomTilt() {
        double tiltX = Math.random() * 20 - 10;
        double tiltY = Math.random() * 20 - 10;
        double tiltZ = Math.random() * 20 - 10;

        Rotate rotateX = new Rotate(tiltX, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(tiltY, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(tiltZ, Rotate.Z_AXIS);

        getTransforms().addAll(rotateX, rotateY, rotateZ);
    }

    public void moveCelestialObject(double[] newPosition) {
        setTranslateX(newPosition[0]/SCALE);
        setTranslateY(newPosition[2]/SCALE);
        setTranslateZ(newPosition[1]/SCALE);
    }
}
