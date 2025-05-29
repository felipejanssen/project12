package Backend.Physics;

import java.util.ArrayList;
import java.util.List;

public class Trajectory {

    double timeStep = -999;
    List<State> states;

    public Trajectory() {
        this.states = new ArrayList<>();
    }

    public void addState(State toAdd) {
        states.add(toAdd);
        if (states.size() > 1 && timeStep == -999) {
            timeStep = states.get(1).getTime() - states.get(0).getTime();
        }
    }

    public State getLastState() {
        return this.states.get(states.size() - 1);
    }

    public List<State> getStates() {
        return states;
    }

    public State getState(double time) {
        if (time < 0)
            throw new IllegalArgumentException(new String("Bro you can't travel back in time :("));
        int i = (int) (time / timeStep);
        if (i >= states.size())
            throw new IllegalArgumentException(new String("Time value exceeds the simulation"));
        else {
            return states.get(i);
        }
    }

    public void print() {
        if (states == null || states.isEmpty()) {
            System.out.println("No trajectory data to display.");
            return;
        }

        System.out.printf("%-10s %-30s %-30s%n", "Time", "Position (x, y, z)", "Velocity (vx, vy, vz)");
        System.out.println("=".repeat(80));

        for (State state : states) {
            double[] pos = state.getPos();
            double[] vel = state.getVel();

            System.out.printf("%-10.2f (%.3f, %.3f, %.3f)    (%.3f, %.3f, %.3f)%n",
                    state.getTime(),
                    pos[0], pos[1], pos[2],
                    vel[0], vel[1], vel[2]);
        }
    }
}
