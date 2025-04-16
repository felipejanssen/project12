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
                double x = Double.parseDouble(data[1]);
                double y = Double.parseDouble(data[2]);
                double z = Double.parseDouble(data[3]);
                double dx = Double.parseDouble(data[4]);
                double dy = Double.parseDouble(data[5]);
                double dz = Double.parseDouble(data[6]);

                double mass = Double.parseDouble(data[7]);
                double radius = estimateRadiusFromMass(mass);
                int ringType = Integer.parseInt(data[8]);
                //double radius = 3;

                Planet planet = new Planet(x, y, z, dx, dy, dz, radius, mass, ringType, name + ".jpg");
                planet.setDepthTest(DepthTest.ENABLE);

                SunAndPlanets.add(planet);
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Error loading Planets " + e);
        }
        return SunAndPlanets;
    }

    /**
     * Function estimates radius based on a set density and fits it to javafx platform.
     * <p>Uses formula to derive radius from volume.</p>
     * <p>Has minimum and maximum</p>
     * <p>A bigger mass would mean a bigger planet in the application.</p>
     *
     * @param massKg The mass of the planetary object
     * @return Returns estimated radius
     */
    private static double estimateRadiusFromMass(double massKg) {
        double density = 10000;
        double volume = massKg / density;
        double scaledRadius = (1e-6)*Math.cbrt((3 * volume) / (4 * Math.PI));

        double minRadius = 1;
        double maxRadius = 50;

        return Math.max(minRadius, Math.min(scaledRadius, maxRadius));
    }




}
