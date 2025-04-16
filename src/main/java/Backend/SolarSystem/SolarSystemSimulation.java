package Backend.SolarSystem;

public class SolarSystemSimulation {
    public static void main(String[] args) {
        //time parameters & step size
        double t0 = 0;
        double h = 3600; //equivalent to 1 hour
        double tn = 365.25 * 24 * 3600; //Total simulation time: 1 year

        //Simulation
        double currentTime = t0;
        while (currentTime <= tn) {
            currentTime += h;
        }
    }
}
