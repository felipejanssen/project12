package Backend.Physics;

import Backend.ODE.ODEsolver;
import Backend.SolarSystem.Planet;
import Utils.vec;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class Newton {
    public static State computeNextPlanetState(Planet target, ArrayList<Planet> others, double time, double h, ODEsolver solver) {
        BiFunction<Double, double[], double[]> ode = (t, state) -> {
            double[] derivative = new double[6];
            double[] pos = new double[] { state[0], state[1], state[2] };
            double[] vel = new double[] { state[3], state[4], state[5] };

            derivative[0] = vel[0];
            derivative[1] = vel[1];
            derivative[2] = vel[2];

            double[] totalAccel = new double[3];
            for (Planet other : others) {
                if (other == target) continue;
                double[] rVec = vec.substract(other.getState().getPos(), pos);
                double dist = vec.magnitude(rVec);
                double forceMag = (6.67430e-20 * other.getMass()) / (dist * dist);
                double[] acc = vec.multiply(vec.normalize(rVec), forceMag);
                totalAccel = vec.add(totalAccel, acc);
            }

            derivative[3] = totalAccel[0];
            derivative[4] = totalAccel[1];
            derivative[5] = totalAccel[2];

            return derivative;
        };

        ODEsolver localSolver = new ODEsolver(ode);
        double[] nextState = localSolver.RK4Step(time, target.getState().getState(), h);
        return new State(time + h, nextState);
    }

    public static ArrayList<State> computeAllNextStates(ArrayList<Planet> planets, double currentTime, double h) {
        ArrayList<State> nextStates = new ArrayList<>();
        for (Planet p : planets) {
            ArrayList<Planet> others = new ArrayList<>(planets);
            nextStates.add(computeNextPlanetState(p, others, currentTime, h, null)); // solver not used, since we pass a custom one
        }
        return nextStates;
    }
}
