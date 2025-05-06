package Backend.SolarSystem;

import Backend.Physics.Impulse;
import Backend.Physics.SolarSystemEngine;
import Backend.Physics.State;
import Backend.Physics.Trajectory;

import java.util.ArrayList;
import java.util.List;

public class SolarSystemSimulator {

    ArrayList<CelestialObject> bodies;
    SolarSystemEngine engine;
    SpaceShip ship;
    List<Impulse> impulses;
    int nextImpulseIndex;

    // Simulation parameters
    double t0 = .0;
    double h = 3600; // 1 hour in seconds
    double endTime = 365.25 * 24 * 3600; // simulate 1 year

    public ArrayList<CelestialObject> getBodies() {
        return this.bodies;
    }

    public SolarSystemSimulator(ArrayList<CelestialObject> bodies) {
        initializeSystem(bodies);
    }

    public SolarSystemSimulator(ArrayList<CelestialObject> bodies, double t0, double h, double endTime, List<Impulse> impulsesList) {
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

        // Create the engine
        this.engine = new SolarSystemEngine(bodies);
    }

//    public Trajectory simulate() {
//
//        initializeSystem();
//        Trajectory shipTrajectory = new Trajectory();
//        double time = t0;
//        int nextImpulseIndex = 0; // First impulse is the first in the list
//
//        // Run the simulation
//        while (time < endTime) {
//            State shipState = ship.getState();
//            shipTrajectory.addState(shipState);
//            if (nextImpulseIndex < impulses.size()) {
//                Impulse nextImpulse = impulses.get(nextImpulseIndex);
//                if (Math.abs(time - nextImpulse.getTime()) < h / 2.0) {
//                    double[] dir = nextImpulse.getNormalizedDir();
//                    double scale = nextImpulse.getMag() / ship.getMass();
//                    ship.applyImpulse(dir, scale); // new applyImpulse takes direction and scale of magnitude
//                    nextImpulseIndex++; // move to the next one
//                }
//            }
//            engine.evolve(time, h);
//            time += h;
//        }
//        return shipTrajectory;
//    }

    public void simulate(double t0) {

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
        //return engine.getCurrentState();
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
