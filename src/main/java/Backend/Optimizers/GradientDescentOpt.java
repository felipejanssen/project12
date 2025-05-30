package Backend.Optimizers;

import Backend.Physics.Impulse;
import Backend.Physics.State;
import Backend.Physics.Trajectory;
import Utils.vec;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GradientDescentOpt extends AbstractOptimizer {

    private double initialLearningRate;
    private double currentLearningRate;
    private List<double[]> momentum;
    private double beta = 0.9;
    private int iterationCount = 0;
    private double bestCostEver = Double.MAX_VALUE;
    private double bestDistanceEver = Double.MAX_VALUE; // NEW: Track best distance separately!
    private List<Impulse> bestSolutionEver;
    private int noImprovementCounter = 0;
    private int noDistanceImprovementCounter = 0; // NEW: Separate counter for distance
    int velocityViolations;

    public GradientDescentOpt(int maxIters, double tol, double lr, double e) {
        super(maxIters, tol, lr, e);
        this.initialLearningRate = lr;
        this.currentLearningRate = lr;
        this.momentum = new ArrayList<>();
    }

    @Override
    protected List<Impulse> update(List<Impulse> impulses) {
        iterationCount++;

        if (momentum.isEmpty()) {
            for (Impulse imp : impulses) {
                momentum.add(new double[imp.getImpulseVec().length]);
            }
            bestSolutionEver = deepCopy(impulses);
            bestDistanceEver = penaltyFor(impulses); // Initialize best distance
        }

        double currentCost = computeCost(impulses);
        double currentDistance = penaltyFor(impulses); // Get pure distance

        double maxVelocity = checkMaxVelocity(impulses);
        System.out.println("🚀 Max velocity in trajectory: " + String.format("%.2f", maxVelocity) + " m/s");

        if (maxVelocity > 60.0) {
            System.out.println("⚠️ VELOCITY VIOLATION! Max allowed: 60 m/s");
            velocityViolations++;
        }

        if (velocityViolations > 10) {
            System.out.println("⚠️ TOO MANY VELOCITY VIOLATIONS! Reducing learning rate aggressively");
            currentLearningRate = initialLearningRate * 0.1;
        } else if (velocityViolations > 5) {
            System.out.println("⚠️ Some velocity violations, reducing learning rate");
            currentLearningRate = initialLearningRate * 0.3;
        }

        // Track BOTH cost improvement AND distance improvement
        boolean costImproved = currentCost < bestCostEver;
        boolean distanceImproved = currentDistance < bestDistanceEver;

        if (costImproved) {
            bestCostEver = currentCost;
            noImprovementCounter = 0;
        } else {
            noImprovementCounter++;
        }

        // NEW: Track distance improvement separately
        if (distanceImproved) {
            bestDistanceEver = currentDistance;
            bestSolutionEver = deepCopy(impulses); // Save best solution based on distance!
            noDistanceImprovementCounter = 0;
            System.out.println("🎯 NEW BEST DISTANCE! " + String.format("%.1f", currentDistance/1000.0) + " km");
        } else {
            noDistanceImprovementCounter++;
        }

        double actualDistanceKm = currentDistance / 1000.0;

        System.out.println("🎯 SMART OPTIMIZER - LR: " + String.format("%.4f", currentLearningRate) +
                " | Best Distance: " + String.format("%.1f", bestDistanceEver/1000.0) + " km" +
                " | Violations: " + velocityViolations +
                " | No Improve: " + noDistanceImprovementCounter); // Use distance counter!

        // Learning rate adjustment based on distance, not total cost
        if (actualDistanceKm > 500000) {
            currentLearningRate = initialLearningRate * 2.0;
        } else if (actualDistanceKm > 100000) {
            currentLearningRate = initialLearningRate * 1.5;
        } else if (actualDistanceKm > 50000) {
            currentLearningRate = initialLearningRate * 1.0;
        } else if (actualDistanceKm > 10000) {
            currentLearningRate = initialLearningRate * 0.8;
        } else {
            currentLearningRate = initialLearningRate * 0.3;
        }

        // MODIFIED: Use distance improvement counter for escape mechanisms
        if (noDistanceImprovementCounter > 8) { // Trigger based on distance, not cost
            System.out.println("🌪️ RANDOM PERTURBATION ESCAPE! (No distance improvement for 8 iterations)");
            List<Impulse> perturbed = deepCopy(bestSolutionEver);
            Random rnd = new Random();

            for (Impulse imp : perturbed) {
                double[] vec = imp.getImpulseVec();
                for (int i = 0; i < vec.length; i++) {
                    vec[i] += vec[i] * (rnd.nextGaussian() * 0.2);
                }
            }

            noDistanceImprovementCounter = 0;
            currentLearningRate = initialLearningRate * 2.0;
            return perturbed;
        }

        if (noDistanceImprovementCounter > 20) {
            System.out.println("💥 BIG EXPLORATION JUMP! (No distance improvement for 20 iterations)");
            List<Impulse> jumped = deepCopy(bestSolutionEver);
            Random rnd = new Random();

            for (Impulse imp : jumped) {
                double[] vec = imp.getImpulseVec();
                for (int i = 0; i < vec.length; i++) {
                    vec[i] += vec[i] * (rnd.nextGaussian() * 0.5);
                }
            }

            noDistanceImprovementCounter = 0;
            currentLearningRate = initialLearningRate * 3.0;
            return jumped;
        }

        List<Impulse> newImpulses = new ArrayList<>();
        Random rnd = new Random();

        for (int impIdx = 0; impIdx < impulses.size(); impIdx++) {
            Impulse imp = impulses.get(impIdx);
            double[] vec = imp.getImpulseVec();
            double[] originalImp = vec.clone();
            double[] newImpulseVec = new double[vec.length];
            double[] m = momentum.get(impIdx);

            for (int i = 0; i < vec.length; i++) {
                double adaptiveEpsilon = Math.max(epsilon * 0.5, Math.abs(vec[i]) * 0.01);

                // Calculate gradient
                imp.changeImpulse(i, adaptiveEpsilon);
                double costP = computeCost(impulses);

                imp.changeImpulse(i, -2 * adaptiveEpsilon);
                double costM = computeCost(impulses);

                double grad = (costP - costM) / (2 * adaptiveEpsilon);
                imp.setImpulse(originalImp);

                // ADD NOISE to escape local minima!
                double noise = rnd.nextGaussian() * currentLearningRate * 0.001;
                grad += noise;

                // Apply momentum
                m[i] = beta * m[i] + currentLearningRate * grad;

                // ALLOW BIGGER CHANGES
                double maxChange = Math.max(0.5, Math.abs(vec[i]) * 0.3);
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

    private double checkMaxVelocity(List<Impulse> impulses) {
        Trajectory[] trajectories = super.simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];

        double maxSpeed = 0.0;
        for (State state : shipTraj.getStates()) {
            double[] velocity = state.getVel();
            double speed = vec.magnitude(velocity);
            maxSpeed = Math.max(maxSpeed, speed);
        }

        return maxSpeed;
    }

    private int countVelocityViolations(List<Impulse> impulses) {
        Trajectory[] trajectories = simulator.simulate(impulses);
        Trajectory shipTraj = trajectories[0];

        int violations = 0;
        final double MAX_VELOCITY = 60.0;

        for (State state : shipTraj.getStates()) {
            double speed = vec.magnitude(state.getVel());
            if (speed > MAX_VELOCITY) {
                violations++;
            }
        }

        return violations;
    }
}