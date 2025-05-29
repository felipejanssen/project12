package Backend.Optimizers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Backend.Physics.Impulse;
import Utils.vec;

public class Optimizer {

    private static final int MAX_ITERATIONS = 1000;
    private static final double TOLERANCE = 1e-2; // Much smaller tolerance
    private static final double LEARNING_RATE = 0.1; // Much larger learning rate
    private static final double EPSILON = 1e-2; // Larger epsilon for finite differences

    private static final GradientDescentOpt opt = new GradientDescentOpt(MAX_ITERATIONS, TOLERANCE, LEARNING_RATE,
            EPSILON);

    public static void main(String[] args) {

        // Use ACTUAL Earth orbital velocity from CSV data
        double[] earthPos = new double[] { -1.474114613044819E+08, -2.972578730668059E+07, 2.745063093019836E+04 };
        double[] earthVel = new double[] { 5.306839723370035E+00, -2.934993232297309E+01, 6.693785809943620E-04 };

        // Calculate direction to Titan
        double[] titanPos = new double[] { 1.421787721861711E+09, -1.917156604354737E+08, -5.275190739154144E+07 };

        double[] toTitan = vec.substract(titanPos, earthPos);
        double[] unitToTitan = vec.normalize(toTitan);

        // Add velocity for interplanetary transfer (Hohmann-like transfer)
        // Need about 3.5 km/s additional velocity for Saturn transfer
        double[] transferVel = vec.multiply(unitToTitan, 3.5);

        // CRITICAL: Start with Earth's velocity + transfer velocity
        double[] initVelocity = vec.add(earthVel, transferVel);

        System.out.println("Earth velocity: " + Arrays.toString(earthVel));
        System.out.println("Transfer velocity: " + Arrays.toString(transferVel));
        System.out.println("Initial velocity: " + Arrays.toString(initVelocity));

        List<Impulse> initImpulses = new ArrayList<>();
        Impulse launchImpulse = new Impulse(initVelocity, 0);
        initImpulses.add(launchImpulse);

        // Add a mid-course correction after 6 months
        double[] midCourseVel = new double[] { 0.1, 0.1, 0.1 };
        Impulse midCourse = new Impulse(midCourseVel, 180 * 24 * 3600); // 6 months
        initImpulses.add(midCourse);

        List<Impulse> optimizedImpulses = opt.optimize(initImpulses);
    }
}