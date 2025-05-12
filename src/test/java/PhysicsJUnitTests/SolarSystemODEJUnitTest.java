package PhysicsJUnitTests;

import Backend.Physics.SolarSystemODE;
import Backend.Physics.State;
import Backend.SolarSystem.CelestialObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class SolarSystemODEJUnitTest {

    private CelestialObject createObject(double[] pos, double[] vel, double mass, boolean spaceship) {
        return new CelestialObject() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            public double getMass() {
                return mass;
            }

            @Override
            public State getState() {
                return new State(0, pos, vel);
            }

            @Override
            public boolean isSpaceship() {
                return spaceship;
            }

            @Override
            public void setState(State state) {
            }

            @Override
            public void setMass(double m) {
            }

            @Override
            public void setScale(double s) {
            }

            @Override
            public void moveCelestialObject(double[] position) {
            }
        };
    }

    @Test
    public void testGravitationalAttractionBetweenTwoBodies() {
        //test for gravitational pull between 2 bodies
        ArrayList<CelestialObject> bodies = new ArrayList<>();
        bodies.add(createObject(new double[]{0, 0, 0}, new double[]{0, 0, 0}, 5.972e24, false)); // planet
        bodies.add(createObject(new double[]{0, 1000, 0}, new double[]{0, 0, 0}, 1000, true));   // spaceship

        //ode gravity function
        BiFunction<Double, double[], double[]> ode = SolarSystemODE.create(bodies);

        //initial state
        double[] state = {
                0, 0, 0, 0, 0, 0,
                0, 1000, 0, 0, 0, 0
        };

        //derivatives(speed)
        double[] derivatives = ode.apply(0.0, state);

        //check speed for planet
        assertEquals(0, derivatives[3], 1e-9); // Earth accel x
        assertEquals(0, derivatives[4], 1e-9); // Earth accel y
        assertEquals(0, derivatives[5], 1e-9); // Earth accel z

        //check speed for spaceship
        assertEquals(0, derivatives[9], 1e-9); // Ship accel x
        assertTrue(derivatives[10] < 0);       // Ship accel y
        assertEquals(0, derivatives[11], 1e-9);// Ship accel z
    }


    @Test
    public void testNoAttractionBetweenSpaceships() {
        ArrayList<CelestialObject> bodies = new ArrayList<>();
        bodies.add(createObject(new double[]{0, 0, 0}, new double[]{0, 0, 0}, 1000, true));
        bodies.add(createObject(new double[]{0, 1000, 0}, new double[]{0, 0, 0}, 1000, true));

        BiFunction<Double, double[], double[]> ode = SolarSystemODE.create(bodies);

        //initial state
        double[] state = {
                0, 0, 0, 0, 0, 0,
                0, 1000, 0, 0, 0, 0
        };

        double[] derivatives = ode.apply(0.0, state);

        //check there is no attraction between spaceships
        for (int i = 3; i < derivatives.length; i++) {
            assertEquals(0, derivatives[i], 1e-9); // All accelerations must be 0
        }
    }

    @Test
    public void testCorrectVelocityDerivatives() {
        ArrayList<CelestialObject> bodies = new ArrayList<>();
        bodies.add(createObject(new double[]{0, 0, 0}, new double[]{1, 2, 3}, 1e20, false));

        BiFunction<Double, double[], double[]> ode = SolarSystemODE.create(bodies);

        //initial state
        double[] state = {
                0, 0, 0, 1, 2, 3
        };

        double[] derivatives = ode.apply(0.0, state);

        //check that the speed is correct
        assertEquals(1, derivatives[0], 1e-9);
        assertEquals(2, derivatives[1], 1e-9);
        assertEquals(3, derivatives[2], 1e-9);

        //check that the acceleration is correct
        assertEquals(0, derivatives[3], 1e-9);
        assertEquals(0, derivatives[4], 1e-9);
        assertEquals(0, derivatives[5], 1e-9);
    }
}