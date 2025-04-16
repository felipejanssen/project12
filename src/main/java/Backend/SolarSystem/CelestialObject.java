package Backend.SolarSystem;

public interface CelestialObject {
    double getMass();
    double[] getState();

    void setState(double[] state);
    void setMass(double weight);
}

