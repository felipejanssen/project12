package Backend.SolarSystem;

import javafx.scene.DepthTest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SolarSystemFunctions {
    public static ArrayList<Planet> GetAllPlanetsPlanetarySystem(String PlanetarySystemPath) {
        ArrayList<Planet> SunAndPlanets = new ArrayList<>();
        try {
            InputStream is = SolarSystemFunctions.class.getClassLoader().getResourceAsStream(PlanetarySystemPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            if (is == null) {
                throw new RuntimeException("File not found: " + PlanetarySystemPath);
            }
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String name = data[0];
                double[] position = {Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])};
                double[] velocity = {Double.parseDouble(data[4]), Double.parseDouble(data[5]), Double.parseDouble(data[6])};
                double mass = Double.parseDouble(data[7]);
                int ringType = Integer.parseInt(data[8]);

                Planet planet = new Planet(name, position, velocity, mass, ringType);
                planet.setDepthTest(DepthTest.ENABLE);

                SunAndPlanets.add(planet);
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Error loading Planets " + e);
        }
        return SunAndPlanets;
    }

    public static double estimateRadiusFromMass(double massKg) {
        double density = 500;
        double volume = massKg / density;
        double scaledRadius = (1e-6)*Math.cbrt((3 * volume) / (4 * Math.PI));

        double minRadius = 1;
        double maxRadius = 200;

        return Math.max(minRadius, Math.min(scaledRadius, maxRadius));
    }
}
