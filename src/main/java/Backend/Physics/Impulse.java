package Backend.Physics;

public class Impulse {

    // which direction
    private double[] direction = new double[3];
    // strength
    private double magnitude;
    // when to apply it
    private double time;

    public Impulse(double[] dir, double mag, double t) {
        this.direction = dir;
        this.magnitude = mag;
        this.time = t;
    }

    public double getFuelCost() {
        return magnitude;
    }

    public double changeMagBy(double value) {
        return this.magnitude += value;
    }
}
