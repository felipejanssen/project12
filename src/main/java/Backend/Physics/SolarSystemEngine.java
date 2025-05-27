package Backend.Physics;

import Backend.SolarSystem.CelestialObject;
import Backend.ODE.ODEsolver;
import Utils.vec;

import java.util.ArrayList;

public class SolarSystemEngine {
    private static final int DIM = 6;
    private ArrayList<CelestialObject> bodies;
    private ODEsolver solver;

    public SolarSystemEngine(ArrayList<CelestialObject> bodies) {
        this.bodies = bodies;

        double[] initialStates = flattenStates();

        solver = new ODEsolver(SolarSystemODE.create(bodies));
    }

    public double[] getCurrentState() {
        return flattenStates();
    }

    public void evolve(double time, double h) {
        // Step forward one timestep
        double[] currentStates = getCurrentState();

        double[] nextStates = solver.RK4Step(time, currentStates, h);

        for (int i = 0; i < bodies.size(); i++) {
            double[] nextState = new double[DIM];
            System.arraycopy(nextStates, i * DIM, nextState, 0, DIM);
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

    // helper method to flatten States in one array
    private double[] flattenStates() {
        double[] flat = new double[bodies.size() * DIM];
        for (int i = 0; i < bodies.size(); i++) {
            System.arraycopy(bodies.get(i).getState().getState(), 0,
                    flat,
                    i * DIM,
                    DIM);
        }
        return flat;
    }

    public double[] getTitanPos() {
        CelestialObject titan = this.bodies.get(8);
        System.out.println(titan.getName());
        double[] pos = titan.getState().getPos();

        return pos;
    }

    public State getTitanState() {
        CelestialObject titan = this.bodies.get(8);

        return titan.getState();
    }

}
