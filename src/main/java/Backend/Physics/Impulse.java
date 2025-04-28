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

    /**
     * Copy constructor - creates a new Impulse that is a copy of the given Impulse
     * @param other The Impulse to copy
     */
    public Impulse(Impulse other) {
        // Create a new direction array to avoid reference sharing
        this.direction = new double[other.direction.length];
        // Copy each element of the direction array
        for (int i = 0; i < other.direction.length; i++) {
            this.direction[i] = other.direction[i];
        }
        // Copy the magnitude and time
        this.magnitude = other.magnitude;
        this.time = other.time;
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
