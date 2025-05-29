package Backend.Optimizers;

import Backend.Physics.Impulse;
import java.util.ArrayList;
import java.util.List;

public class MomentumGradientDescentOpt extends AbstractOptimizer {

    private double momentum = 0.9;
    private List<double[]> velocities;
    private double initialLearningRate;
    private double currentLearningRate;

    public MomentumGradientDescentOpt(int maxIters, double tol, double lr, double e) {
        super(maxIters, tol, lr, e);
        this.initialLearningRate = lr;
        this.currentLearningRate = lr;
        this.velocities = new ArrayList<>();
    }

    @Override
    protected List<Impulse> update(List<Impulse> impulses) {
        // Initialize velocities on first run
        if (velocities.isEmpty()) {
            for (Impulse imp : impulses) {
                velocities.add(new double[imp.getImpulseVec().length]);
            }
        }

        // Adaptive learning rate
        double currentDistance = computeCost(impulses);
        if (currentDistance < 500000) {
            currentLearningRate = initialLearningRate * 0.1;
        }
        if (currentDistance < 100000) {
            currentLearningRate = initialLearningRate * 0.01;
        }

        List<Impulse> newImpulses = new ArrayList<>();

        for (int impIdx = 0; impIdx < impulses.size(); impIdx++) {
            Impulse imp = impulses.get(impIdx);
            double[] vec = imp.getImpulseVec();
            double[] originalImp = vec.clone();
            double[] newImpulseVec = new double[vec.length];
            double[] velocity = velocities.get(impIdx);

            for (int i = 0; i < vec.length; i++) {
                double adaptiveEpsilon = Math.max(epsilon, Math.abs(vec[i]) * 0.001);

                // Calculate gradient
                imp.changeImpulse(i, adaptiveEpsilon);
                double costP = computeCost(impulses);

                imp.changeImpulse(i, -2 * adaptiveEpsilon);
                double costM = computeCost(impulses);

                double grad = (costP - costM) / (2 * adaptiveEpsilon);
                imp.setImpulse(originalImp);

                // Update velocity with momentum
                velocity[i] = momentum * velocity[i] + currentLearningRate * grad;

                // Apply velocity
                newImpulseVec[i] = vec[i] - velocity[i];
            }

            newImpulses.add(new Impulse(newImpulseVec, imp.getTime()));
        }

        return newImpulses;
    }
}