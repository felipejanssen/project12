package Backend.Optimizers;

import java.util.ArrayList;
import java.util.List;

import Backend.Physics.Impulse;

public class Optimizer {

    private static final int MAX_ITERATIONS = 1000;
    private static final double TOLERANCE = 1e-4;
    private static final double LEARNING_RATE = 0.001;
    private static final double EPSILON = 1e-4;

    private static final GradientDescentOpt opt = new GradientDescentOpt(MAX_ITERATIONS, TOLERANCE, LEARNING_RATE,
            EPSILON);

    public static void main(String[] args) {
        List<Impulse> initImpulses = new ArrayList<>();
        Impulse launchImpulse = new Impulse(new double[] { 1, 3, .2 }, 0);
        initImpulses.add(launchImpulse);

        List<Impulse> optimizedImpulses = opt.optimize(initImpulses);
    }
}