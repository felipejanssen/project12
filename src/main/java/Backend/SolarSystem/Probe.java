package Backend.SolarSystem;

public class Probe extends SpaceVessel{
    public Probe(String vesselName, double[] position, double[] velocity, double mass){
        super(vesselName, position, velocity, mass);
    }
    @Override
    public boolean isSpaceship(){
        return false;
    }
    @Override
    public boolean isProbe(){
        return true;
    }
}

