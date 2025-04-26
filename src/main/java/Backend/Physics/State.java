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
        System.out.println("Satte before: " + this);
        this.velocity = vec.add(this.velocity, vel);
        System.out.println("State after: " + this);
    }

    @Override
    public String toString() {
        return String.format(
                "State at time %.2f s:\n" +
                        "  Position -> (x: %.2f km, y: %.2f km, z: %.2f km)\n" +
                        "  Velocity -> (vx: %.2f km/s, vy: %.2f km/s, vz: %.2f km/s)",
                time,
                position[0], position[1], position[2],
                velocity[0], velocity[1], velocity[2]);
    }
}
