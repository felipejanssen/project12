package Backend.Optimizers;

import Backend.Physics.Impulse;
import Utils.vec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Optimizer {

    private static final int MAX_ITERATIONS = 1000;
    private static final double TOLERANCE = 1e-3;
    private static final double LEARNING_RATE = 10; // Much more reasonable
    private static final double EPSILON = 1e-4;

    // Use a MUCH better optimizer
    private static final GradientDescentOpt opt = new GradientDescentOpt(
            MAX_ITERATIONS, TOLERANCE, LEARNING_RATE, EPSILON);

    public static void main(String[] args) {
        // Earth data from your simulation
        double[] earthPos = new double[] { -1.474114613044819E+08, -2.972578730668059E+07, 2.745063093019836E+04 };
        double[] earthVel = new double[] { 5.306839723370035E+00, -2.934993232297309E+01, 6.693785809943620E-04 };
        double[] titanPos = new double[] { 1.421787721861711E+09, -1.917156604354737E+08, -5.275190739154144E+07 };

        System.out.println("Earth pos: " + Arrays.toString(earthPos));
        System.out.println("Earth vel: " + Arrays.toString(earthVel));
        System.out.println("Titan pos: " + Arrays.toString(titanPos));

        // Calculate proper Hohmann transfer
        double[] toTitan = vec.substract(titanPos, earthPos);
        double distanceToTitan = vec.magnitude(toTitan);
        double[] unitToTitan = vec.normalize(toTitan);

        System.out.println("Initial distance to Titan: " + (distanceToTitan / 1000.0) + " km");

        // MUCH smaller initial velocity boost - for interplanetary transfers
        // Saturn is ~9.5 AU from Sun, Earth is ~1 AU
        // Proper Hohmann transfer velocity boost should be ~3.6 km/s, not 11.2!
        double[] transferVel = vec.multiply(unitToTitan, 3.6); // Realistic transfer velocity
        double[] initVelocity = vec.add(earthVel, transferVel);

        System.out.println("Earth velocity: " + Arrays.toString(earthVel));
        System.out.println("Transfer velocity: " + Arrays.toString(transferVel));
        System.out.println("Initial velocity: " + Arrays.toString(initVelocity));

        List<Impulse> initImpulses = new ArrayList<>();
        Impulse launchImpulse = new Impulse(initVelocity, 0);
        initImpulses.add(launchImpulse);

        // MUCH smaller course corrections - you were using way too big values
        double[] midCourseVel1 = new double[] { 0.01, 0.01, 0.01 }; // 10 m/s corrections
        Impulse midCourse1 = new Impulse(midCourseVel1, 90 * 24 * 3600); // 3 months
        initImpulses.add(midCourse1);

        double[] midCourseVel2 = new double[] { 0.005, 0.005, 0.005 }; // 5 m/s corrections
        Impulse midCourse2 = new Impulse(midCourseVel2, 180 * 24 * 3600); // 6 months
        initImpulses.add(midCourse2);

        System.out.println("🚀 LAUNCHING SMART TRAJECTORY OPTIMIZER! 🚀");
        List<Impulse> optimizedImpulses = opt.optimize(initImpulses);
        System.out.println("🎯 OPTIMIZATION COMPLETE! 🎯");
    }
}