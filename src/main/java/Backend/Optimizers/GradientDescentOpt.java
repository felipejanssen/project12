package Backend.Optimizers;

import Backend.Physics.Impulse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradientDescentOpt extends AbstractOptimizer {

    private static final int MAX_ITERATIONS = 1000;
    private static final double TOLERANCE = 1e-4;
    private static final double LEARNING_RATE = 0.001;
    private static final double EPSILON = 1e-4;

    public GradientDescentOpt() {
        super(MAX_ITERATIONS, TOLERANCE, LEARNING_RATE, EPSILON);
    }

    public GradientDescentOpt(int maxIters, double tol, double lr, double e) {
        super(maxIters, tol, lr, e);
    }

    @Override
    protected List<Impulse> update(List<Impulse> impulses) {

        List<Impulse> newImpulses = new ArrayList<>();
        double[] originalImp;
        for (Impulse imp : impulses) {
            double[] vec = imp.getImpulseVec();
            originalImp = vec.clone();
            double[] newImpulseVec = new double[vec.length];
            for (int i = 0; i < vec.length; i++) {
                // Positiove change
                imp.changeImpulse(i, epsilon);
                double costP = computeCost(impulses);
                // System.out.println("Cost P: " + costP);
                // Negative change
                imp.changeImpulse(i, -2 * epsilon);
                double costM = computeCost(impulses);
                // System.out.println("Cost M: " + costM);
                double costDiff = (costP - costM);
                // System.out.println("Cost diff = " + costDiff);
                double grad = costDiff / (2 * epsilon);
                // Reset
                imp.setImpulse(originalImp);
                // Adjust
                // System.out.println("Gradient = " + grad);
                // System.out.println("Adjustment = " + (learningRate * grad));

                newImpulseVec[i] = vec[i] - (learningRate * grad);
            }
            // Add modified impulse to the list
            newImpulses.add(new Impulse(newImpulseVec, imp.getTime()));
        }
        return newImpulses;
    }
}
