package PhysicsJUnitTests;

import Backend.Physics.Newton;
import Backend.Physics.State;
import Backend.SolarSystem.CelestialObject;
import Backend.SolarSystem.Planet;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewtonJUnitTest {

    @Test
    public void testComputeNextPlanetState(){
        //setting up 2 celestial obj
        CelestialObject target = new Planet("Earth",new double[]{0,0,0} ,new double[]{0,0,0}, 5.972e24, 6371 );

        CelestialObject other = new Planet("Moon", new double[]{384400,0,0}, new double[]{0,1,0}, 7.342e22,1737);

        ArrayList<CelestialObject> others = new ArrayList<>();
        others.add(target);
        others.add(other);

        //compute next state for earth
        State nextState = Newton.computeNextPlanetState(target, others, 0.0, 60,null);

        assertNotNull(nextState);
        assertEquals(6, nextState.getState().length);
    }

    @Test
    public void testComputeAllNextStates(){
        //creating 2 obj: earth+moon
        CelestialObject target = new Planet("Earth",new double[]{0,0,0} ,new double[]{0,0,0}, 5.972e24, 6371 );

        CelestialObject other = new Planet("Moon", new double[]{384400,0,0}, new double[]{0,1,0}, 7.342e22,1737);

        ArrayList<CelestialObject> objects = new ArrayList<>();
        objects.add(target);
        objects.add(other);

        //currenttime+stepsize
        double currentTime = 0.0;
        double h =60;

        ArrayList<State> nextStates = Newton.computeAllNextStates(objects, currentTime,h);


        //check 2 states were returned, 1 for each obj
        assertEquals(2, nextStates.size());

        //verify vectors length 6
        for (State state: nextStates){
            assertEquals(6, state.getState().length);
        }

    }
}
