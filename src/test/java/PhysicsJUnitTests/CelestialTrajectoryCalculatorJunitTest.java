package PhysicsJUnitTests;

import Backend.Physics.CelestialTrajectoryCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CelestialTrajectoryCalculatorJunitTest {

    @Test
    public void TestComputeGravitationalDerivatives() {
        //configuration of initial state for 3 bodies
        int numBodies= 3;
        double[] state = new double[numBodies *6];
        double[] masses = {1.0, 1.0, 1.0};

        //body1
        state[0] = 1.0; state[1] = 0.0; state[2] = 0.0;//position
        state[3] = 1.0; state[4] = 0.0; state[5] = 0.0;//speed

        //bpdy2
        state[6] = 2.0; state[7] = 0.0; state[8] = 0.0;
        state[9] = 1.0; state[10] = 0.0; state[11] = 0.0;

        //body3
        state[12] = 3.0; state[13] = 0.0; state[14] = 0.0;
        state[15] = 1.0; state[16] = 0.0; state[17] = 0.0;
        double[] derivatives = CelestialTrajectoryCalculator.computeGravitationalDerivatives(0.0, state, masses);

        //verifying if the speed components and acceleration are correct

        assertEquals(1.0, derivatives[0], 1e-6);
        assertEquals(0.0, derivatives[1], 1e-6);
        assertEquals(0.0, derivatives[2], 1e-6);

        //verify acceleration
        assertEquals(0.0, derivatives[3], 1e-6);
        assertEquals(0.0, derivatives[4], 1e-6);
        assertEquals(0.0, derivatives[5], 1e-6);

    }

    @Test
    public void testCalculateTrajectory() {
        //initial values configuration for 1 body with speed
        double[] initialState = {1.0, 0.0, 0.0,
                                 1.0, 0.0, 0.0};
        double[] masses = {1.0};
        double t0=0.0;
        double timeStep = 1.0;
        int steps = 10;

        double[] trajectory = CelestialTrajectoryCalculator.calculateTrajectory(initialState, masses, t0, timeStep, steps);

        //veryfing the trajectory results
        assertNotNull(trajectory);
        assertEquals(trajectory.length, initialState.length);

        //x position should move
        double expectedX = initialState[0] + initialState[3] *timeStep *steps;
        double expectedY = initialState[1] + initialState[4] *timeStep *steps;
        double expectedZ = initialState[2] + initialState[5] *timeStep *steps;

        double errorX =Math.abs(expectedX -trajectory[0]);
        double errorY =Math.abs(expectedY -trajectory[2]);
        double errorZ =Math.abs(expectedZ -trajectory[2]);

        assertEquals(expectedX, trajectory[0], 1e-6);
        assertEquals(expectedY, trajectory[1], 1e-6);
        assertEquals(expectedZ, trajectory[2], 1e-6);

    }

}
