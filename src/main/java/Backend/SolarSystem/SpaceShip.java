package Backend.SolarSystem;

import Utils.vec;

public class SpaceShip extends SpaceVessel implements Thrust{
    private double currentFuel;
    public SpaceShip(String vesselName, double[] position, double[] velocity, double mass, double currentFuel) {
        super(vesselName, position, velocity, mass);
        this.currentFuel = currentFuel;
    }

    @Override
    public boolean isSpaceship(){
        return true;
    }
    @Override
    public boolean isProbe(){
        return false;
    }

    @Override
    public void applyThrust() {}

    public void setCurrentFuel(double currentFuel) {}
    public double getCurrentFuel() {return currentFuel;}
    public void consumeFuel(double amount) {this.currentFuel -= amount;}


    public boolean applyImpulse(double[] direction, double magnitude) {
        // Check if we have enough fuel
        if (this.currentFuel <= 0) {
            System.out.println("Out of fuel!");
            return false;
        }

        // Normalize the direction vector
        double[] normalizedDir = vec.normalize(direction);

        // Calculate velocity change based on impulse and mass
        double scale = magnitude / mass;
        double[] deltaV = vec.multiply(normalizedDir, scale);

        // Get current velocity
        double[] currentVel = state.getVel();



        // Update velocity with the capped deltaV
        state.addVel(deltaV);

        return true;
    }
}
