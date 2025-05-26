package PhysicsJUnitTests;

import Backend.Physics.State;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StateJUnitTest {

    @Test
    public void testStateConstructor() {
        double time=0.0;
        double[] position = {1.0, 2.0, 3.0};
        double[] velocity = {0.1, 0.2, 0.3};

        //create object
        State state = new State(time, position, velocity);
        assertEquals(time,state.getTime());
        assertArrayEquals(position, state.getPos());
        assertArrayEquals(velocity, state.getVel());
    }

    @Test
    public void testStateFromStateArray() {

        double time=0.0;

        double[] stateArray = {1.0, 2.0, 3.0, 0.1, 0.2, 0.3};

        //create new obj
        State state = new State(time, stateArray);

        assertEquals(time,state.getTime());
        assertArrayEquals(new double[]{1.0,2.0,3.0}, state.getPos());
        assertArrayEquals(new double[]{0.1,0.2,0.3}, state.getVel());
    }

    @Test
    public void testAddVelocity() {
        //initialize position+speed
        double[] position = {1.0, 2.0, 3.0};
        double[] velocity = {0.1, 0.2, 0.3};

        //new obj
        State state = new State(0.0, position, velocity);

        //s[eed that we want to add
        double[] addedVelocity = {0.5, 0.5, 0.5};

        state.addVel(addedVelocity);

        assertArrayEquals(new double[]{0.6,0.7,0.8},state.getVel());
    }

    @Test
    public void testGetState() {
        double[] position = {1.0, 2.0, 3.0};
        double[] velocity = {0.1, 0.2, 0.3};

        //new obj
        State state = new State(0.0, position, velocity);

        //check if the return is the same
        double[] expectedState = {1.0,2.0,3.0,0.1,0.2,0.3};
        assertArrayEquals(expectedState, state.getState());

    }
}
