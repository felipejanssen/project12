package Backend.SolarSystem;

import Backend.Physics.SolarSystemEngine;
import Backend.Physics.State;

import java.util.ArrayList;

public class SolarSystemSimulator {

    public static void main(String[] args) {

        // Simulation parameters
        double t0 = .0;
        double h = 3600; // 1 hour in seconds
        double endTime = 365.25 * 24 * 3600; // simulate 1 year

        printState(simulate(t0, h, endTime));

    }

    public static double[] simulate(double t0, double h, double endTime) {

        // Load planets from CSV
        ArrayList<CelestialObject> solarSystemPlanets = SolarSystemFunctions
                .GetAllPlanetsPlanetarySystem("SolarSystemValues.csv");

        // Convert to generic CelestialObjects (could include spacecraft later!)
        ArrayList<CelestialObject> bodies = new ArrayList<>(solarSystemPlanets);

        // Create the engine
        SolarSystemEngine engine = new SolarSystemEngine(bodies);

        double time = t0;

        // Run the simulation
        while (time < endTime) {
            engine.evolve(time, h);
            time += h;
        }
        System.out.println("Simulation complete.");
        return engine.getCurrentState();
    }

    public static double[] simulate(double t0, double h, double endTime, boolean print) {

        // Load planets from CSV
        ArrayList<CelestialObject> solarSystemPlanets = SolarSystemFunctions
                .GetAllPlanetsPlanetarySystem("SolarSystemValues.csv");

        // Convert to generic CelestialObjects (could include spacecraft later!)
        ArrayList<CelestialObject> bodies = new ArrayList<>(solarSystemPlanets);

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

    private static void printState(double[] state) {
        if (state.length != 66) {
            System.err.println("Invalid state length: expected 66 but got " + state.length);
            return;
        }

        for (int i = 0; i < 11; i++) {
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
