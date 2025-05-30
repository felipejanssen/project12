package Backend.SolarSystem;

import Utils.vec;
import Backend.Physics.Impulse;
import Backend.Physics.SolarSystemEngine;
import Backend.Physics.State;
import Backend.Physics.Trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolarSystemSimulator {

    String csvpath = "SolarSystemValues.csv";

    ArrayList<CelestialObject> bodies;
    SolarSystemEngine engine;
    SpaceShip ship;
    List<Impulse> impulses;
    int nextImpulseIndex;
    final double MAX_VELOCITY = 60.0; // Velocity cap


    // Simulation parameters
    double t0 = .0;
    double h = 3600; // 1 hour in seconds
    double endTime = 365.25 * 24 * 3600; // simulate 1 year

    public ArrayList<CelestialObject> getBodies() {
        return this.bodies;
    }

    public SolarSystemSimulator() {
        resetSim();
    }

    public SolarSystemSimulator(ArrayList<CelestialObject> bodies) {
        initializeSystem(bodies);
    }

    public SolarSystemSimulator(ArrayList<CelestialObject> bodies, double t0, double h, double endTime,
            List<Impulse> impulsesList) {
        this.t0 = t0;
        this.h = h;
        this.endTime = endTime;
        this.impulses = impulsesList;
        if (impulsesList != null) {
            this.nextImpulseIndex = 0;
        }
        initializeSystem(bodies);
    }

    public void initializeSystem(ArrayList<CelestialObject> bodies) {

        this.bodies = new ArrayList<>(bodies);
        this.ship = (SpaceShip) bodies.getLast();
        // Create the engine
        this.engine = new SolarSystemEngine(bodies);
    }

    private void resetSim() {
        ArrayList<CelestialObject> bodies = SolarSystemFunctions.GetAllPlanetsPlanetarySystem(csvpath);
        this.ship = SolarSystemFunctions.getNewShip();
        bodies.add(this.ship);
        initializeSystem(bodies);
    }

    public Trajectory[] simulate(List<Impulse> imp) {
        resetSim();
        Trajectory shipTrajectory = new Trajectory();
        Trajectory titanTrajectory = new Trajectory();
        double time = 0;
        int nextIndex = 0; // First impulse is the first in the list

        // Run the simulation
        while (time < endTime) {
            State shipState = ship.getState();
            shipTrajectory.addState(shipState);
            titanTrajectory.addState(engine.getTitanState());
            if (nextIndex < imp.size()) {
                Impulse nextImpulse = imp.get(nextIndex);
                double nextTime = nextImpulse.getTime();
                if (Math.abs(time - nextTime) < h / 2.0) {
                    double[] dir = nextImpulse.getNormalizedDir();
                    // System.out.println("Applying impulse: ");
                    // System.out.println("dir: " + Arrays.toString(dir));
                    double scale = nextImpulse.getMag() / ship.getMass();
                    // System.out.println("Scale: " + scale);
                    ship.applyImpulse(dir, scale); // new applyImpulse takes direction and scale of magnitude
                    nextIndex++; // move to the next one
                }
            }
            engine.evolve(time, h);


            double[] postPhysicsVel = ship.getState().getVel();
            double postPhysicsSpeed = vec.magnitude(postPhysicsVel);
            if (postPhysicsSpeed > MAX_VELOCITY) {
                double[] cappedVel = vec.multiply(vec.normalize(postPhysicsVel), MAX_VELOCITY);
                ship.getState().setVel(cappedVel);
            }

            time += h;
        }
        return new Trajectory[] { shipTrajectory, titanTrajectory };
    }

    public void simulate(double t0) {

//        if (ship == null) {
//            throw new IllegalStateException("Ship has not been initialized");
//        }

        double time = t0;
        if (nextImpulseIndex < impulses.size()) {
            Impulse nextImpulse = impulses.get(nextImpulseIndex);
            if (Math.abs(time - nextImpulse.getTime()) < h / 2.0) {
                double[] dir = nextImpulse.getNormalizedDir();
                double scale = nextImpulse.getMag() / ship.getMass();
                ship.applyImpulse(dir, scale); // new applyImpulse takes direction and scale of magnitude
                nextImpulseIndex++; // move to the next one
            }
        }
        // Run the simulation
        engine.evolve(time, h);
        // return engine.getCurrentState();
    }

    private void printState(double[] state) {
        if (state.length % 6 != 0) {
            System.err.println("Invalid state length: " + state.length + " not divisible by 6");
            return;
        }
        int nBodies = state.length / 6;

        for (int i = 0; i < nBodies; i++) {
            int index = i * 6;
            double x = state[index];
            double y = state[index + 1];
            double z = state[index + 2];
            double vx = state[index + 3];
            double vy = state[index + 4];
            double vz = state[index + 5];

            System.out.printf("Body %2d:\n", i + 1);
            System.out.printf("  Position: (%.6e, %.6e, %.6e)\n", x, y, z);
            System.out.printf("  Velocity: (%.6e, %.6e, %.6e)\n\n", vx, vy, vz);
        }
    }
}
