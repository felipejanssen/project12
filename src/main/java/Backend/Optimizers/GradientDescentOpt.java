//package Backend.Optimizers;
//
//import Backend.Physics.Impulse;
//import Backend.SolarSystem.SolarSystemSimulator;
//
//import java.util.List;
//
//
//public class GradientDescentOpt extends AbstractOptimizer {
//    private final SolarSystemSimulator simulator;
//
//    public GradientDescentOpt(SolarSystemSimulator sim,
//                                    double lr, double fd, double tol, int maxIters)
//    {
//        super(maxIters, tol, lr, fd);
//        this.simulator = sim;
//    }
//
//    @Override
//    protected void step(List<Impulse> impulses) {
//        for (Impulse imp : impulses) {
//            imp.changeMagBy(finiteDifference);
//            double costP = computeCost(impulses);
//            imp.changeMagBy(-2*finiteDifference);
//            double costM = computeCost(impulses);
//            double grad  = (costP - costM)/(2*finiteDifference);
//            imp.changeMagBy(finiteDifference);               // reset
//            imp.changeMagBy(-learningRate * grad);
//        }
//    }
//
//    @Override
//    protected double penaltyFor(List<Impulse> impulses) {
//        return simulator.simulate(impulses).computePenalty(); // or inline your penalty fn
//    }
//}
