package Backend.Physics;

import Backend.SolarSystem.CelestialObject;
import Backend.ODE.ODEsolver;
import Utils.vec;

import java.util.ArrayList;

public class SolarSystemEngine {

    private ArrayList<CelestialObject> bodies;
    private ODEsolver solver;

    public SolarSystemEngine(ArrayList<CelestialObject> bodies) {
        this.bodies = bodies;

        double[] initialStates = new double[bodies.size() * 6];
        for (int i = 0; i < bodies.size(); i++) {
            System.arraycopy(bodies.get(i).getState().getState(), 0, initialStates, i * 6, 6);
        }

        solver = new ODEsolver(SolarSystemODE.create(bodies));
    }

    public double[] getCurrentState() {
        double[] currentStates = new double[bodies.size() * 6];
        for (int i = 0; i < bodies.size(); i++) {
            System.arraycopy(bodies.get(i).getState().getState(), 0, currentStates, i * 6, 6);
        }
        return currentStates;
    }

    public void evolve(double time, double h) {
        // Step forward one timestep
        double[] currentStates = getCurrentState();

        double[] nextStates = solver.RK4Step(time, currentStates, h);

        for (int i = 0; i < bodies.size(); i++) {
            double[] nextState = new double[6];
            System.arraycopy(nextStates, i * 6, nextState, 0, 6);
            bodies.get(i).setState(new State(time + h, nextState));
        }
        // ANCHOR THE SYSTEM TO THE SUN
        CelestialObject sun = bodies.get(0);
        double[] sunPos = sun.getState().getPos();
        double[] sunVel = sun.getState().getVel();
        // CHANGE SUN MOVEMENT TO ACCOMODAATE THAT MOVEMENT
        for (CelestialObject obj : bodies) {
            State old = obj.getState();
            double[] newPos = vec.substract(old.getPos(), sunPos);
            double[] newVel = vec.substract(old.getVel(), sunVel);
            obj.setState(new State(old.getTime(), newPos, newVel));
        }
    }

}
