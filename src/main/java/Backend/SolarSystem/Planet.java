package Backend.SolarSystem;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;


/**
 * The {@code Planet} class extends {@code Sphere} to give it additional information needed to represent a planet in a javafx scene.
 * Adds State vector
 * Adds Weight value
 * Adds general Gravitational Constant
 * Tries to apply texture
 *
 */
public class Planet extends Sphere {
    private final double[] State = new double[6];
    private final double weight;
    private final double GravitationalConstant = 6.6743*Math.pow(10, -11);

    public Planet(double x, double y, double z, double dx, double dy, double dz, double radius, double weight, String texturePath) {
        super(radius);
        State[0] = x;
        State[1] = y;
        State[2] = z;
        State[3] = dx;
        State[4] = dy;
        State[5] = dz;
        this.weight = weight;
        setTexture(texturePath);
    }

    public double[] getState() {
        return State;
    }
    public double getWeight() {
        return weight;
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
            this.setMaterial(material);

        } catch (Exception e) {
            System.err.println("Error loading texture: " + e.getMessage());
            this.setMaterial(new PhongMaterial(Color.GRAY));
        }
    }

}
