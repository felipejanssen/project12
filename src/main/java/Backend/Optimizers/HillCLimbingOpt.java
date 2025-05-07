//package Backend.Optimizers;
//
//import Backend.Physics.Impulse;
//import Backend.SolarSystem.SolarSystemSimulator;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//// HillClimbingOptimizer.java
//public class HillCLimbingOpt extends AbstractOptimizer {
//    private final SolarSystemSimulator simulator;
//    private final Random rnd = new Random();
//
//    public HillCLimbingOpt(SolarSystemSimulator sim, int maxIters, double stepSize, double tol)
//    {
//        // learningRate & finiteDifference unused here
//        super(maxIters, tol, 0, 0);
//        this.simulator = sim;
//        this.finiteDifference = stepSize;
//    }
//
//    @Override
//    protected void step(List<Impulse> impulses) {
//        double bestCost = computeCost(impulses);
//        List<Impulse> original = copy(impulses);
//
//        // try perturbing each impulse
//        for (Impulse imp : impulses) {
//            double d = (rnd.nextDouble()*2 - 1)*finiteDifference;
//            imp.changeMagBy(d);
//
//            double c = computeCost(impulses);
//            if (c < bestCost) {
//                bestCost = c;  // keep this change
//            } else {
//                // revert
//                int idx = impulses.indexOf(imp);
//                impulses.set(idx, new Impulse(original.get(idx)));
//            }
//        }
//    }
//
//    @Override
//    protected double penaltyFor(List<Impulse> impulses) {
//        return simulator.simulate(impulses).computePenalty();
//    }
//
//    private List<Impulse> copy(List<Impulse> src) {
//        List<Impulse> dst = new ArrayList<>(src.size());
//        for (Impulse i : src) dst.add(new Impulse(i));
//        return dst;
//    }
//}
