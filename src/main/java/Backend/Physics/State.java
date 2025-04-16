package Backend.Physics;

public class State {

    private double time;
    private double[] position = new double[3];
    private double[] velocity = new double[3];

    public State(double t, double[] pos, double[] vel) {
        this.time = t;
        this.position = pos;
        this.velocity = vel;
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
}
