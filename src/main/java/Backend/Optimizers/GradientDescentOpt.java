package Backend.Optimizers;

import Backend.Physics.Impulse;
import java.util.ArrayList;
import java.util.List;

public class GradientDescentOpt extends AbstractOptimizer {

    private double initialLearningRate;
    private double currentLearningRate;
    private List<double[]> momentum;
    private double beta = 0.9;
    private int iterationCount = 0;
    private double bestCostEver = Double.MAX_VALUE;
    private List<Impulse> bestSolutionEver;
    private int noImprovementCounter = 0;

    public GradientDescentOpt(int maxIters, double tol, double lr, double e) {
        super(maxIters, tol, lr, e);
        this.initialLearningRate = lr;
        this.currentLearningRate = lr;
        this.momentum = new ArrayList<>();
    }

    @Override
    protected List<Impulse> update(List<Impulse> impulses) {
        iterationCount++;

        // Initialize momentum on first run
        if (momentum.isEmpty()) {
            for (Impulse imp : impulses) {
                momentum.add(new double[imp.getImpulseVec().length]);
            }
            bestSolutionEver = deepCopy(impulses);
        }

        double currentCost = computeCost(impulses);

        // Track best solution ever found
        if (currentCost < bestCostEver) {
            bestCostEver = currentCost;
            bestSolutionEver = deepCopy(impulses);
            noImprovementCounter = 0;
        } else {
            noImprovementCounter++;
        }

        // Adaptive learning rate based on distance
        double actualDistanceMeters = penaltyFor(impulses); // This is the real distance penalty
        double actualDistanceKm = actualDistanceMeters / 1000.0; // Real distance in km

        System.out.println("🔍 DEBUG - Actual distance: " + String.format("%.1f", actualDistanceKm) + " km, Total cost: " + String.format("%.1f", currentCost));

        if (actualDistanceKm > 500000) {
            currentLearningRate = initialLearningRate * 2.0; // Aggressive for far distances
        } else if (actualDistanceKm > 100000) {
            currentLearningRate = initialLearningRate * 1.0; // Normal
        } else if (actualDistanceKm > 50000) {
            currentLearningRate = initialLearningRate * 0.5; // Careful
        } else if (actualDistanceKm > 10000) {
            currentLearningRate = initialLearningRate * 0.1; // Very careful
        } else {
            currentLearningRate = initialLearningRate * 0.01; // Ultra precise
        }

        // If no improvement for too long, return to best known solution and try again
        if (noImprovementCounter > 10) {
            System.out.println("🔄 RESETTING TO BEST KNOWN SOLUTION! Distance: " + (bestCostEver/1000.0) + " km");
            noImprovementCounter = 0;
            currentLearningRate = initialLearningRate * 1.5; // Boost learning rate
            return bestSolutionEver;
        }

        List<Impulse> newImpulses = new ArrayList<>();

        for (int impIdx = 0; impIdx < impulses.size(); impIdx++) {
            Impulse imp = impulses.get(impIdx);
            double[] vec = imp.getImpulseVec();
            double[] originalImp = vec.clone();
            double[] newImpulseVec = new double[vec.length];
            double[] m = momentum.get(impIdx);

            for (int i = 0; i < vec.length; i++) {
                // Much more conservative epsilon
                double adaptiveEpsilon = Math.max(epsilon * 0.1, Math.abs(vec[i]) * 0.001);

                // Calculate gradient
                imp.changeImpulse(i, adaptiveEpsilon);
                double costP = computeCost(impulses);

                imp.changeImpulse(i, -2 * adaptiveEpsilon);
                double costM = computeCost(impulses);

                double grad = (costP - costM) / (2 * adaptiveEpsilon);
                imp.setImpulse(originalImp);

                // Apply momentum
                m[i] = beta * m[i] + currentLearningRate * grad;

                // Conservative update with clipping
                double maxChange = Math.max(0.1, Math.abs(vec[i]) * 0.1); // Max 10% change
                double update = Math.max(-maxChange, Math.min(maxChange, m[i]));

                newImpulseVec[i] = vec[i] - update;
            }

            newImpulses.add(new Impulse(newImpulseVec, imp.getTime()));
        }



        return newImpulses;
    }

    private List<Impulse> deepCopy(List<Impulse> impulses) {
        List<Impulse> copy = new ArrayList<>();
        for (Impulse imp : impulses) {
            copy.add(imp.clone());
        }
        return copy;
    }
}