package Backend.SolarSystem;

import Backend.Physics.State;

public interface CelestialObject {
    String getName();

    double getMass();
    State getState();

    boolean isSpaceship();
    boolean isProbe();

    void setState(State state);

    void setMass(double mass);

    void setScale(double scale);

    void moveCelestialObject(double[] position);
}
