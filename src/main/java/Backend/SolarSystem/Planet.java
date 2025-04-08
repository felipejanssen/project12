package Backend.SolarSystem;

public class Planet {
    private final double[] State = new double[6];

    public Planet(double x, double y, double z, double dx, double dy, double dz) {
        State[1] = x;
        State[2] = y;
        State[3] = z;
        State[4] = dx;
        State[5] = dy;
        State[6] = dz;
    }

    public double[] getState() {
        return State;
    }
}
