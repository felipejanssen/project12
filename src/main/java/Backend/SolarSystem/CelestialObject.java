package Backend.SolarSystem;

import Backend.Physics.State;

public interface CelestialObject {
    double getMass();
    State getState();

    void setState(State state);
    void setMass(double mass);

    void moveCelestialObject(double[] position);
    double[] scaleforFX(double[] position);
}

