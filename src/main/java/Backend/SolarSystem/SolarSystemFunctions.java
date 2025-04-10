package Backend.SolarSystem;

import javafx.scene.Group;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SolarSystemFunctions {
    public static Group GetAllPlanetsPlanetarySystem(String PlanetarySystemPath) {
        Group SunAndPlanets = new Group();
        try {
            InputStream is = SolarSystemFunctions.class.getClassLoader().getResourceAsStream(PlanetarySystemPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            if (is == null) {
                throw new RuntimeException("File not found: " + PlanetarySystemPath);
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String name = data[0];
                double x = Double.parseDouble(data[1]);
                double y = Double.parseDouble(data[2]);
                double z = Double.parseDouble(data[3]);
                double dx = Double.parseDouble(data[4]);
                double dy = Double.parseDouble(data[5]);
                double dz = Double.parseDouble(data[6]);

                double mass = Double.parseDouble(data[7]);
                double radius = estimateRadiusFromMass(mass);
                //double radius = 30;

                Planet planet = new Planet(x, y, z, dx, dy, dz, radius, mass, name + ".jpg");
                SunAndPlanets.getChildren().add(planet);
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Error loading Planets " + e);
        }
        return SunAndPlanets;
    }

    /**
     * Function estimates radius based on Earth's density and fits it to javafx platform.
     * Uses formula to derive radius from volume.
     * A bigger mass would mean a bigger planet in the application.
     *
     * @param massKg The mass of the planetary object
     * @return Returns estimated radius
     */
    private static double estimateRadiusFromMass(double massKg) {
        double density = 5500;
        double volume = massKg / density;
        double scaledRadius = (1e-6)*Math.cbrt((3 * volume) / (4 * Math.PI));

        double minRadius = 1;
        double maxRadius = 50;

        return Math.max(minRadius, Math.min(scaledRadius, maxRadius));
    }




}
