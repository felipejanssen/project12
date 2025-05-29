package Backend.Optimizers;

import Backend.Physics.Impulse;
import java.util.ArrayList;
import java.util.List;

public class ImprovedGradientDescentOpt extends AbstractOptimizer {

    private double initialLearningRate;
    private double currentLearningRate;
    private int iterationCount = 0;

    public ImprovedGradientDescentOpt(int maxIters, double tol, double lr, double e) {
        super(maxIters, tol, lr, e);
        this.initialLearningRate = lr;
        this.currentLearningRate = lr;
    }

    @Override
    protected List<Impulse> update(List<Impulse> impulses) {
        iterationCount++;

        // Adaptive learning rate - decay as we get closer
        double currentDistance = computeCost(impulses);
        if (currentDistance < 500000) { // Within 500,000 km
            currentLearningRate = initialLearningRate * 0.1;
        }
        if (currentDistance < 100000) { // Within 100,000 km
            currentLearningRate = initialLearningRate * 0.01;
        }
        if (currentDistance < 50000) { // Very close
            currentLearningRate = initialLearningRate * 0.001;
        }

        List<Impulse> newImpulses = new ArrayList<>();
        double[] originalImp;

        for (Impulse imp : impulses) {
            double[] vec = imp.getImpulseVec();
            originalImp = vec.clone();
            double[] newImpulseVec = new double[vec.length];

            for (int i = 0; i < vec.length; i++) {
                // Use adaptive epsilon based on current values
                double adaptiveEpsilon = Math.max(epsilon, Math.abs(vec[i]) * 0.001);

                // Positive change
                imp.changeImpulse(i, adaptiveEpsilon);
                double costP = computeCost(impulses);

                // Negative change
                imp.changeImpulse(i, -2 * adaptiveEpsilon);
                double costM = computeCost(impulses);

                double grad = (costP - costM) / (2 * adaptiveEpsilon);

                // Reset
                imp.setImpulse(originalImp);

                // Apply gradient with current learning rate
                newImpulseVec[i] = vec[i] - (currentLearningRate * grad);
            }

            newImpulses.add(new Impulse(newImpulseVec, imp.getTime()));
        }

        System.out.println("Current learning rate: " + currentLearningRate);
        return newImpulses;
    }
}