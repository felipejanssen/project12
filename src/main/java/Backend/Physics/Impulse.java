package Backend.Physics;

import Utils.vec;

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

    // TODO: Implement better fuel cost estimation
    public double getFuelCost() {
        return magnitude;
    }

    public double[] getDir() {
        return this.direction;
    }

    public double[] getNormalizedDir() {
        return vec.normalize(this.direction);
    }

    public double getTime() {
        return this.time;
    }

    public double getMag() {
        return this.magnitude;
    }

    public double changeMagBy(double value) {
        return this.magnitude += value;
    }
}
