package Backend.SolarSystem;

import java.util.List;
import java.util.function.BiFunction;

import Backend.ODE.ODEsolver;
import Backend.Physics.Impulse;
import Backend.Physics.Trajectory;
import Utils.vec;
import Backend.Physics.State;

public class SolarSystem {

    // Planets
    @SuppressWarnings("unused")
    private static final Planet Earth = new Planet("Earth", new double[] { 420, 420, 420 },
            new double[] { 69, 69, 69 }, 69420, 0);

    // time parameters & step size
    private static final double t0 = 0;
    private static final double h = 3600; // equivalent to 1 hour
    private static final double tn = 365.25 * 24 * 3600; // Total simulation time: 1 year

    // spaceship
    private static final double massSS = 50000; // kg

    // ODE
    private static ODEsolver solver;

    public static void main(String[] args) {

        // Set up ODE
        initializeODE();
    }

    // TODO: Implement the computation of gravity for multiple planets
    private static double[] computeGravity(double[] position, Double time) {

        // double[] earthPos = Earth.getPosition(time); // TODO: Create a function to
        // get it's postion -> double[3]
        double[] earthPos = new double[] { 100, 100, 100 };
        double G = 6.67430e-20; // km^3 / kg / s^2
        double massEarth = 5.972e24; // kg

        double[] r = vec.substract(position, earthPos);
        double distance = vec.magnitude(r);
        double scale = G * massEarth / (distance * distance);
        double[] acceleration = vec.multiply(vec.normalize(r), scale);

        return acceleration;

    }

    // TODO: Set correct starting position for Rocket
    private static State initialConditions() {

        double startTime = 0;
        double[] startPos = new double[] { 0, 0, 0 }; // Yes, we launch from the middle of the sun
        double[] startVel = new double[] { 0, 0, 0 }; // Grounded and stable

        State init = new State(startTime, startPos, startVel);
        return init;
    }

    private static void initializeODE() {

        BiFunction<Double, double[], double[]> ode = (time, state) -> {
            double[] derivative = new double[6];

            // Unpack state: [x, y, z, vx, vy, vz]
            double[] position = new double[] { state[0], state[1], state[2] };
            double[] velocity = new double[] { state[3], state[4], state[5] };

            // 1. Derivative of position is velocity
            derivative[0] = velocity[0];
            derivative[1] = velocity[1];
            derivative[2] = velocity[2];

            // 2. Compute acceleration from gravity (and optionally thrust)
            double[] acceleration = computeGravity(position, time);

            derivative[3] = acceleration[0];
            derivative[4] = acceleration[1];
            derivative[5] = acceleration[2];

            return derivative;
        };

        solver = new ODEsolver(ode);

    }

    /**
     * This method is called to simulate a rocket launch
     * 
     * @param impulses Rocket thrusts and other external impulses
     * @return the trajectory of the rocket containin all states
     */
    public static Trajectory simulateMission(List<Impulse> impulses) {

        Trajectory trajectory = new Trajectory();

        double currentTime = t0;
        State currentState = initialConditions();
        int nextImpulseIndex = 0; // First impulse is the first in the list

        while (currentTime < tn) {

            trajectory.addState(currentState);

            if (nextImpulseIndex < impulses.size()) {
                Impulse nextImpulse = impulses.get(nextImpulseIndex);
                if (Math.abs(currentTime - nextImpulse.getTime()) < h / 2.0) {
                    double[] dir = nextImpulse.getNormalizedDir();
                    double scale = nextImpulse.getMag() / massSS;
                    double[] deltaV = vec.multiply(dir, scale);
                    currentState.addVel(deltaV);
                    nextImpulseIndex++; // move to the next one
                }
            }

            // Use your ODE solver to get next state
            double[] nextState = solver.RK4Step(currentTime, currentState.getState(), h);
            currentTime += h;
            currentState = new State(currentTime, nextState);
        }

        return trajectory;
    }

}
