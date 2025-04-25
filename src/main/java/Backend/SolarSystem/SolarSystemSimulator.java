package Backend.SolarSystem;

import Backend.Physics.SolarSystemEngine;
import Backend.Physics.State;

import java.util.ArrayList;

public class SolarSystemSimulator {

    public static void main(String[] args) {

        // Load planets from CSV
        ArrayList<CelestialObject> solarSystemPlanets = SolarSystemFunctions
                .GetAllPlanetsPlanetarySystem("SolarSystemValues.csv");

        // Convert to generic CelestialObjects (could include spacecraft later!)
        ArrayList<CelestialObject> bodies = new ArrayList<>(solarSystemPlanets);

        // Create the engine
        SolarSystemEngine engine = new SolarSystemEngine(bodies);

        // Simulation parameters
        double time = 0;
        double h = 3600; // 1 hour in seconds
        double endTime = 365.25 * 24 * 3600; // simulate 1 year

        // Run the simulation
        while (time < endTime) {
            engine.evolve(time, h);
            time += h;

            if ((int)(time / h) % 24 == 0) { // print once per day
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
    }
}
