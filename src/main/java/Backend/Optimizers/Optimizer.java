package Backend.Optimizers;

import Backend.Physics.Impulse;
import Utils.vec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Optimizer {

    private static final int MAX_ITERATIONS = 2000; // Increased iterations
    private static final double TOLERANCE = 1e-3; // Slightly relaxed tolerance
    private static final double LEARNING_RATE = 0.5; // Reduced initial learning rate
    private static final double EPSILON = 1e-3; // Slightly larger epsilon

    // Use the improved optimizer
    private static final ImprovedGradientDescentOpt opt = new ImprovedGradientDescentOpt(
            MAX_ITERATIONS, TOLERANCE, LEARNING_RATE, EPSILON);

    public static void main(String[] args) {
        double[] earthPos = new double[] { -1.474114613044819E+08, -2.972578730668059E+07, 2.745063093019836E+04 };
        double[] earthVel = new double[] { 5.306839723370035E+00, -2.934993232297309E+01, 6.693785809943620E-04 };
        double[] titanPos = new double[] { 1.421787721861711E+09, -1.917156604354737E+08, -5.275190739154144E+07 };

        double[] toTitan = vec.substract(titanPos, earthPos);
        double[] unitToTitan = vec.normalize(toTitan);
        double[] transferVel = vec.multiply(unitToTitan, 11.2);
        double[] initVelocity = vec.add(earthVel, transferVel);

        List<Impulse> initImpulses = new ArrayList<>();
        Impulse launchImpulse = new Impulse(initVelocity, 0);
        initImpulses.add(launchImpulse);

        // Add multiple course corrections for better control
        double[] midCourseVel1 = new double[] { 0.1, 0.1, 0.1 };
        Impulse midCourse1 = new Impulse(midCourseVel1, 90 * 24 * 3600); // 3 months
        initImpulses.add(midCourse1);

        double[] midCourseVel2 = new double[] { 0.05, 0.05, 0.05 };
        Impulse midCourse2 = new Impulse(midCourseVel2, 180 * 24 * 3600); // 6 months
        initImpulses.add(midCourse2);

        List<Impulse> optimizedImpulses = opt.optimize(initImpulses);
    }
}