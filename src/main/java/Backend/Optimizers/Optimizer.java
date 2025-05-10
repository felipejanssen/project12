package Backend.Optimizers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Backend.Physics.Impulse;
import Utils.vec;

public class Optimizer {

    private static final int MAX_ITERATIONS = 1000;
    private static final double TOLERANCE = 10; // for now it mean 10km basically
    private static final double LEARNING_RATE = 0.001;
    private static final double EPSILON = 1e-4;

    private static final GradientDescentOpt opt = new GradientDescentOpt(MAX_ITERATIONS, TOLERANCE, LEARNING_RATE,
            EPSILON);

    public static void main(String[] args) {

        // Estimate starting velocity
        double[] earth = new double[] { -1.47e8, -2.97e7, 2.75e4 };
        double[] titan = new double[] { 1.42e9, -1.92e8, -5.28e7 };

        double[] dir = vec.substract(titan, earth);
        double[] unitDir = vec.normalize(dir);
        double[] initVelocity = vec.multiply(unitDir, 12); // after consulting with GPT, I concluded that 12km/s is
                                                           // advised

        System.out.println(Arrays.toString(initVelocity));
        List<Impulse> initImpulses = new ArrayList<>();
        Impulse launchImpulse = new Impulse(initVelocity, 0);
        initImpulses.add(launchImpulse);

        List<Impulse> optimizedImpulses = opt.optimize(initImpulses);
    }
}