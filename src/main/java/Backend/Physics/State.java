package Backend.Physics;

import Utils.vec;

public class State {

    private double time;
    private double[] position = new double[3];
    private double[] velocity = new double[3];

    public State(double t, double[] pos, double[] vel) {
        this.time = t;
        this.position = pos;
        this.velocity = vel;
    }

    public State(double t, double[] state) {
        this.time = t;
        for (int i = 0; i < 6; i++) {
            if (i < 3)
                this.position[i] = state[i];
            else
                this.velocity[i - 3] = state[i];
        }
    }

    public double getTime() {
        return this.time;
    }

    public double[] getPos() {
        return this.position;
    }

    public double[] getVel() {
        return this.velocity;
    }

    public double[] getState() {

        double[] state = new double[6];
        for (int i = 0; i < 6; i++) {
            state[i] = i < 3 ? this.position[i] : this.velocity[i - 3];
        }

        return state;
    }

    /**
     * Adds velocity to the current velocity of the state
     * 
     * @param vel
     */
    public void addVel(double[] vel) {
        this.velocity = vec.add(this.velocity, vel);
    }
}
