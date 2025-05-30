package Backend.SolarSystem;

import javafx.scene.DepthTest;
import javafx.scene.transform.Scale;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SolarSystemFunctions {
    public static ArrayList<CelestialObject> GetAllPlanetsPlanetarySystem(String PlanetarySystemPath) {
        ArrayList<CelestialObject> SunAndPlanets = new ArrayList<>();
        try {
            InputStream is = SolarSystemFunctions.class.getClassLoader().getResourceAsStream(PlanetarySystemPath);
            if (is == null) {
                throw new RuntimeException("File not found: " + PlanetarySystemPath);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String name = data[0];
                double[] position = { Double.parseDouble(data[1]), Double.parseDouble(data[2]),
                        Double.parseDouble(data[3]) };
                double[] velocity = { Double.parseDouble(data[4]), Double.parseDouble(data[5]),
                        Double.parseDouble(data[6]) };
                double mass = Double.parseDouble(data[7]);
                int ringType = Integer.parseInt(data[8]);

                Planet planet = new Planet(name, position, velocity, mass, ringType);
                planet.setDepthTest(DepthTest.ENABLE);
                planet.getTransforms().add(new Scale(0.2, 0.2, 0.2));

                SunAndPlanets.add(planet);
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Error loading Planets " + e);
        }
        return SunAndPlanets;
    }

    public static double estimateRadiusFromMass(double massKg) {
        double density = 1000;
        double volume = massKg / density;
        double scaledRadius = (1e-6) * Math.cbrt((3 * volume) / (4 * Math.PI));

        double minRadius = 5;
        double maxRadius = 50;

        return Math.max(minRadius, Math.min(scaledRadius, maxRadius));
    }

    // TODO: set proper starting state
    public static SpaceShip getNewShip() {

        double[] pos = new double[] { -1.47E+08, -2.97E+07, 2.75E+04 };
        double[] vel = new double[] {5.306839723370035E+00,-2.934993232297309E+01,6.693785809943620E-04};
        double m = 50000;
        double initFuel = 9999;

        return new SpaceShip("Nebuchadnezzar", pos, vel, m, Double.MAX_VALUE);
    }
}
