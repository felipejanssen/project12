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
}
