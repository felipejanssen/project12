package Backend.SolarSystem;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * The {@code Planet} class extends {@code Group} to give it additional information needed to represent a planet in a javafx scene.
 * Adds State vector
 * Adds mass value
 * Tries to apply texture
 *
 */
public class Planet extends Group implements CelestialObject {
    private double[] State = new double[6];
    private double mass;
    private final double radius;

    private final Sphere sphere;

    private static final double xScale = 1e6;
    private static final double yScale = 1e6;
    private static final double zScale = 1e6;

    public Planet(double x, double y, double z, double dx, double dy, double dz, double radius, double mass, int ringType, String texturePath) {
        this.sphere = new Sphere(radius);
        this.mass = mass;
        this.radius = radius;

        setState(x, y, z, dx, dy, dz);
        setTexture(texturePath);
        if(ringType != 0)
            setRing(ringType);
        scaleforFX(x,y,z);

        addRandomTilt();

        getChildren().add(sphere);

        setDepthTest(DepthTest.ENABLE);

    }

    public double[] getState() {
        return State;
    }
    public double getMass() {
        return mass;
    }

    public void setState(double[] state) {
        State = state;
    }
    public void setState(double x, double y, double z, double dx, double dy, double dz) {
        State[0] = x;
        State[1] = y;
        State[2] = z;
        State[3] = dx;
        State[4] = dy;
        State[5] = dz;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }


    /**
     * Tries to apply texture to Planet object, by using the given path to a texture png.
     * If none is available it will assign the texture to be gray.
     *
     * @param texturePath The filepath to the texture png
     */
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
    private void scaleforFX(double x, double y, double z) {
        setTranslateX(x / xScale);
        setTranslateY(y / yScale);
        setTranslateZ(z / zScale);
    }

}
