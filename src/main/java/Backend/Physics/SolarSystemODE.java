package Backend.Physics;

import Backend.SolarSystem.CelestialObject;
import Utils.vec;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class SolarSystemODE {

    @SuppressWarnings("unused")
    public static BiFunction<Double, double[], double[]> create(ArrayList<CelestialObject> bodies) {
        return (time, state) -> {
            int n = bodies.size();
            double[] derivatives = new double[n * 6]; // [x, y, z, vx, vy, vz] * n

            for (int i = 0; i < n; i++) {
                int idx = i * 6;

                // Current body position and velocity
                double[] pos_i = { state[idx], state[idx + 1], state[idx + 2] };
                double[] vel_i = { state[idx + 3], state[idx + 4], state[idx + 5] };

                // d(pos)/dt = velocity
                derivatives[idx] = vel_i[0];
                derivatives[idx + 1] = vel_i[1];
                derivatives[idx + 2] = vel_i[2];

                // d(vel)/dt = acceleration from all others
                double[] acc = new double[3];

                for (int j = 0; j < n; j++) {
                    if (i == j)
                        continue;

                    int jdx = j * 6;
                    double[] pos_j = { state[jdx], state[jdx + 1], state[jdx + 2] };
                    double m_j = bodies.get(j).getMass();

                    double[] r_ij = vec.substract(pos_j, pos_i);
                    double dist = vec.magnitude(r_ij);
                    if (dist == 0)
                        continue;

                    double G = 6.67430e-20;
                    double scale = G * m_j / (dist * dist);
                    acc = vec.add(acc, vec.multiply(vec.normalize(r_ij), scale));
                }

                derivatives[idx + 3] = acc[0];
                derivatives[idx + 4] = acc[1];
                derivatives[idx + 5] = acc[2];
            }

            return derivatives;
        };
    }
}
