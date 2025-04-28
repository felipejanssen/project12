package Backend.SolarSystem;

import Backend.Physics.Impulse;
import Backend.Physics.SolarSystemEngine;
import Backend.Physics.State;
import Backend.Physics.Trajectory;
import Utils.vec;

import java.util.ArrayList;
import java.util.List;

public class SolarSystemSimulator {

    ArrayList<CelestialObject> bodies;
    SolarSystemEngine engine;
    SpaceShip ship;

    // Simulation parameters
    double t0 = .0;
    double h = 3600; // 1 hour in seconds
    double endTime = 365.25 * 24 * 3600; // simulate 1 year

    public SolarSystemSimulator() {
        initializeSystem();
    }

    public SolarSystemSimulator(double t0, double h, double endTime) {
        this.t0 = t0;
        this.h = h;
        this.endTime = endTime;
        initializeSystem();
    }

    public void initializeSystem() {

        // Load planets from CSV
        ArrayList<CelestialObject> solarSystemPlanets = SolarSystemFunctions
                .GetAllPlanetsPlanetarySystem("SolarSystemValues.csv");

        this.bodies = new ArrayList<>(solarSystemPlanets);
        // Add the spaceship as the last body
        ship = SolarSystemFunctions.getNewShip();
        bodies.add(ship);

        // Create the engine
        this.engine = new SolarSystemEngine(bodies);
    }

    public Trajectory simulate(List<Impulse> impulses) {

        initializeSystem();
        Trajectory shipTrajectory = new Trajectory();
        double time = t0;
        int nextImpulseIndex = 0; // First impulse is the first in the list

        // Run the simulation
        while (time < endTime) {
            State shipState = ship.getState();
            shipTrajectory.addState(shipState);
            if (nextImpulseIndex < impulses.size()) {
                Impulse nextImpulse = impulses.get(nextImpulseIndex);
                if (Math.abs(time - nextImpulse.getTime()) < h / 2.0) {
                    double[] dir = nextImpulse.getNormalizedDir();
                    double scale = nextImpulse.getMag() / ship.getMass();
                    double[] deltaV = vec.multiply(dir, scale);
                    ship.applyImpulse(dir, scale); // new applyImpulse takes direction and scale of magnitude
                    nextImpulseIndex++; // move to the next one
                }
            }
            engine.evolve(time, h);
            time += h;
        }
        return shipTrajectory;
    }

    public double[] simulate(double t0, double h, double endTime, boolean print) {

        // Load planets from CSV
        ArrayList<CelestialObject> solarSystemPlanets = SolarSystemFunctions
                .GetAllPlanetsPlanetarySystem("SolarSystemValues.csv");

        // Convert to generic CelestialObjects (could include spacecraft later!)
        ArrayList<CelestialObject> bodies = new ArrayList<>(solarSystemPlanets);
        SpaceShip ship = SolarSystemFunctions.getNewShip();
        bodies.add(ship);
        // Create the engine
        SolarSystemEngine engine = new SolarSystemEngine(bodies);

        double time = t0;

        // Run the simulation
        while (time < endTime) {
            engine.evolve(time, h);
            time += h;

            if (((int) (time / h) % 24 == 0) && time != 0) { // print once per day
                System.out.printf("=== Time: %.2f hours ===%n", time / 3600);
                for (CelestialObject obj : bodies) {
                    State s = obj.getState();
                    double[] pos = s.getPos();
                    System.out.printf("%-10s: (%.2f, %.2f, %.2f)%n",
                            obj.getName(), pos[0], pos[1], pos[2]);
                }
                System.out.println();
            }
        }
        System.out.println("Simulation complete.");
        return engine.getCurrentState();
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
