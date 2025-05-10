package Backend.Optimizers;

import Backend.Physics.Impulse;

import java.util.ArrayList;
import java.util.List;

public class GradientDescentOpt extends AbstractOptimizer {

    private static final double LEARNING_RATE = 0.001;
    private static final double TOLERANCE = 1e-4;
    private static final double EPSILON = 1e-4;
    private static final int MAX_ITERATIONS = 1000;

    public GradientDescentOpt() {
        super(MAX_ITERATIONS, TOLERANCE, LEARNING_RATE, EPSILON);
    }

    public GradientDescentOpt(double lr, double fd, double tol, int maxIters) {
        super(maxIters, tol, lr, fd);
    }

    @Override
    protected List<Impulse> update(List<Impulse> impulses) {

        List<Impulse> newImpulses = new ArrayList<>();
        for (Impulse imp : impulses) {
            double[] vec = imp.getImpulseVec();
            double[] newImpulseVec = new double[vec.length];
            for (int i = 0; i < vec.length; i++) {
                // Positiove change
                imp.changeImpulse(i, epsilon);
                double costP = computeCost(impulses);
                // Negative change
                imp.changeImpulse(i, -2 * epsilon);
                double costM = computeCost(impulses);
                double grad = (costP - costM) / (2 * epsilon);
                // Reset
                imp.changeImpulse(i, epsilon);
                // Adjust
                newImpulseVec[i] = vec[i] + (-learningRate * grad);
            }
            // Add modified impulse to the list
            newImpulses.add(new Impulse(newImpulseVec, imp.getTime()));
        }
        return newImpulses;
    }
}
