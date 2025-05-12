package PhysicsJUnitTests;

import Backend.Physics.Trajectory;
import Backend.Physics.State;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrajectoryJUnitTest {

    @Test
    public void testAddAndGetLastState(){
        //test add and get last were mostly identical so i just combined them
        Trajectory trajectory = new Trajectory();

        State state1 = new State(0.0, new double[]{1.0,2.0,3.0,0.0,0.0,0.0});
        State state2 = new State(0.0, new double[]{1.1,2.1,3.1,0.1,0.1,0.1});

        trajectory.addState(state1);
        trajectory.addState(state2);

        State lastState = trajectory.getLastState();
        assertEquals(state2, lastState,"Last state should be state 2");
    }

    @Test
    public void testGetstateAtSpecificTime(){
        //we check if getSTate will return the exact state for a specific time
        Trajectory trajectory = new Trajectory();
        State state0 = new State(0.0, new double[]{0,0,0,0,0,0});
        State state1 = new State(1.0, new double[]{1,1,1,1,1,1});
        State state2 = new State(2.0, new double[]{2,2,2,2,2,2});

        trajectory.addState(state0);
        trajectory.addState(state1);
        trajectory.addState(state2);

        //we want the state for 1.0
        State result= trajectory.getState(1.0);

        double[] expectedPos = new double[]{1,1,1};
        double[] expectedVel = new double[]{1,1,1};

        assertArrayEquals(expectedPos, result.getPos(), 1e-9);
        assertArrayEquals(expectedVel, result.getVel(), 1e-9);
    }

    @Test
    public void testGetStateOutOfBounds(){
        Trajectory trajectory = new Trajectory();

        State state1 = new State(0.0, new double[]{0,0,0,0,0,0});
        State state2 = new State(1.0, new double[]{1,1,1,1,1,1});

        trajectory.addState(state1);
        trajectory.addState(state2);

        assertThrows(IllegalArgumentException.class, () -> {
            trajectory.getState(2.0);
        }, "An exception should be thrown for trying to reach a state after the trajectory limit");
    }
    @Test
    public void testGetStateNegativeTime(){
        //case that the wanted time doesn't exist

        Trajectory trajectory = new Trajectory();
        State state1 = new State(0.0, new double[]{1.0,2.0,3.0,0.0,0.0,0.0});

        trajectory.addState(state1);

        //we see the exception thrown for a negative time value
        assertThrows(IllegalArgumentException.class, ()-> {
            trajectory.getState(-1.0);

        }, "We can't travel back in time, so an exception should be thrown");
    }
}
