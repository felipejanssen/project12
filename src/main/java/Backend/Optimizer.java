package Backend;

import java.util.List;

import Backend.Physics.Impulse;
import Backend.Physics.Trajectory;
import Backend.SolarSystem.SolarSystemSimulation;

public class Optimizer {

    private double LEARNING_RATE = 0.001;

    public void gradientDescent(List<Impulse> impulses) {

        double epsilon = 1e-4;
        for (Impulse imp : impulses) {
            imp.changeMagBy(epsilon);
            double costPlus = computeCost(impulses);

            imp.changeMagBy(-2 * epsilon);
            double costMinus = computeCost(impulses);

            double gradient = (costPlus - costMinus) / (2 * epsilon);

            imp.changeMagBy(epsilon); // reset
            imp.changeMagBy(-LEARNING_RATE * gradient);
        }

    }

    public double computeCost(List<Impulse> impulses) {

        double totalFuel = .0;
        Trajectory path = SolarSystemSimulation.simulateMission(impulses);

        for (Impulse imp : impulses) {
            totalFuel += imp.getFuelCost(); // use CI = ||I|| * 1 m/s
        }

        // Add penalties for missing target
        double penalty = computePenalty(path);

        return totalFuel + penalty;
    }

    private double computePenalty(Trajectory path) {
        return Double.POSITIVE_INFINITY;
    }

}
