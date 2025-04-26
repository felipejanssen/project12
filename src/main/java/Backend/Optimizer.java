package Backend;

import java.util.List;

import Backend.Physics.Impulse;
import Backend.Physics.Trajectory;
import Backend.SolarSystem.SolarSystemSimulator;

public class Optimizer {

    SolarSystemSimulator simulator = new SolarSystemSimulator();

    private double LEARNING_RATE = 0.001;
    private double tolerance = 1e-4;
    private List<Impulse> impulses;

    public Optimizer(List<Impulse> impulses) {
        this.impulses = impulses;
    }

    public void gradientDescent() {

        double lastCost = Double.POSITIVE_INFINITY;
        int maxIters = 1000;
        for (int i = 0; i < maxIters; i++) {

            updateImpulses();

            double currentCost = computeCost(impulses);
            if (Math.abs(lastCost - currentCost) < tolerance) {
                break;
            }
        }

    }

    private void updateImpulses() {
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
        Trajectory path = simulator.simulate(impulses);

        for (Impulse imp : impulses) {
            totalFuel += imp.getFuelCost(); // CI = ||I|| * 1 m/s
        }
        double penalty = computePenalty(path);

        return totalFuel + penalty;
    }

    // TODO: Implement real penalty (e.g. based on distance from goal)
    private double computePenalty(Trajectory path) {
        return Double.POSITIVE_INFINITY;
    }

}
