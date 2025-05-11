package Backend.Physics;

import java.util.Arrays;

import Utils.vec;

public class Impulse {

    // which direction
    private double[] direction = new double[3];
    // strength
    private double magnitude;
    // when to apply it
    private double time;

    private double[] impulseVec;

    public Impulse(double[] dir, double mag, double t) {
        this.direction = dir;
        this.magnitude = mag;
        this.time = t;
    }

    public Impulse(double[] impVec, double t) {
        this.impulseVec = impVec;
        this.time = t;
    }

    /**
     * Copy constructor - creates a new Impulse that is a copy of the given Impulse
     * 
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
        this.impulseVec = other.impulseVec;
    }

    public Impulse clone() {
        return new Impulse(this);
    }

    public double[] getImpulseVec() {
        return this.impulseVec;
    }

    public void changeImpulse(int pos, double value) {
        // System.out.println("at: " + pos + " - before: " +
        // Arrays.toString(this.impulseVec));
        this.impulseVec[pos] += value;
        // System.out.println("at: " + pos + " - after: " +
        // Arrays.toString(this.impulseVec));
    }

    public void setImpulse(double[] vec) {
        this.impulseVec = vec;
    }

    // TODO: Implement better fuel cost estimation
    public double getFuelCost() {
        return getMag();
    }

    /**
     * 
     * @return NOT normalized direction vector
     */
    public double[] getDir() {
        return this.impulseVec;
    }

    public double[] getNormalizedDir() {
        return vec.normalize(this.impulseVec);
    }

    public double getTime() {
        return this.time;
    }

    /**
     * 
     * @return vec.magnitude(impulseVec)
     */
    public double getMag() {
        return vec.magnitude(impulseVec);
    }

    public double changeMagBy(double value) {
        return this.magnitude += value;
    }
}
