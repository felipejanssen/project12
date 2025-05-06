// package Backend;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;

// import Backend.Physics.Impulse;
// import Backend.Physics.Trajectory;
// import Backend.SolarSystem.SolarSystemSimulator;

// public class Optimizer {

// SolarSystemSimulator simulator = new SolarSystemSimulator();

// private double LEARNING_RATE = 0.001;
// private double tolerance = 1e-4;
// private List<Impulse> impulses;

// public Optimizer(List<Impulse> impulses) {
// this.impulses = impulses;
// }

// public void gradientDescent() {

// double lastCost = Double.POSITIVE_INFINITY;
// int maxIters = 1000;
// for (int i = 0; i < maxIters; i++) {

// updateImpulses();

// double currentCost = computeCost(impulses);
// if (Math.abs(lastCost - currentCost) < tolerance) {
// break;
// }
// }

// }

// private void updateImpulses() {
// double epsilon = 1e-4;
// for (Impulse imp : impulses) {
// imp.changeMagBy(epsilon);
// double costPlus = computeCost(impulses);

// imp.changeMagBy(-2 * epsilon);
// double costMinus = computeCost(impulses);

// double gradient = (costPlus - costMinus) / (2 * epsilon);

// imp.changeMagBy(epsilon); // reset
// imp.changeMagBy(-LEARNING_RATE * gradient);
// }
// }

// public double computeCost(List<Impulse> impulses) {

// double totalFuel = .0;
// Trajectory path = simulator.simulate(impulses);

// for (Impulse imp : impulses) {
// totalFuel += imp.getFuelCost(); // CI = ||I|| * 1 m/s
// }
// double penalty = computePenalty(path);

// return totalFuel + penalty;
// }

// public double hillClimbing(int maxIterations, double stepSize) {
// double currentCost = computeCost(impulses);
// double bestCost = currentCost;
// List<Impulse> bestImpulses = new ArrayList<>();

// for (Impulse imp : impulses) {
// bestImpulses.add(new Impulse(imp));

// }
// Random random = new Random();
// for (int i = 0; i < maxIterations; i++) {
// List<Impulse> tempImpulses = new ArrayList<>();
// for (Impulse imp : impulses) {
// tempImpulses.add(new Impulse(imp));
// }
// for (Impulse imp : tempImpulses) {
// double perturbation = (random.nextDouble() * 2 -1) * stepSize;
// imp.changeMagBy(perturbation);
// }
// double newCost = computeCost(tempImpulses);
// if (newCost < currentCost){
// currentCost = newCost;

// for (int j = 0; j < impulses.size(); j++ ) {
// impulses.get(j).changeMagBy(tempImpulses.get(j).getMag() -
// impulses.get(j).getMag());

// }
// if (newCost < bestCost){
// bestCost = newCost;
// bestImpulses.clear();
// for (Impulse imp : impulses) {
// bestImpulses.add(new Impulse(imp));
// }
// }
// }
// if (currentCost < bestCost) {
// break;
// }
// }
// impulses.clear();
// impulses.addAll(bestImpulses);

// return bestCost;
// }

// // TODO: Implement real penalty (e.g. based on distance from goal)
// private double computePenalty(Trajectory path) {
// return Double.POSITIVE_INFINITY;
// }

// }
