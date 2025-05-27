package PhysicsJUnitTests;

import Backend.Physics.SolarSystemEngine;
import Backend.Physics.State;
import Backend.SolarSystem.CelestialObject;
import Backend.Physics.SolarSystemODE;
import Backend.ODE.ODEsolver;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SolarSystemEngineJUnitTest {

    @Test
    public void testEvolveSunAndEarthState(){
        //create 9 real bodies
        //there were 2 ways either create a mock of the entire sustem or this

        ArrayList<CelestialObject> bodies = new ArrayList<>();

        //initializing the bodies in simple states

        CelestialObject sun =new CelestialObject() {
            private State state= new State(0.0, new double[]{0,0,0,0,0,0});
            @Override
            public String getName() {
                return "Sun";
            }

            @Override
            public double getMass() {
                return 1.989e30;
            }

            @Override
            public State getState() {
                return state;
            }

            @Override
            public boolean isSpaceship() {
                return false;
            }
            @Override
            public boolean isProbe(){
                return false;
            }
            @Override
            public void setState(State s) {
            this.state = s;
            }

            @Override
            public void setMass(double mass) {

            }

            @Override
            public void setScale(double scale) {

            }

            @Override
            public void moveCelestialObject(double[] position) {

            }
        };

        CelestialObject earth =new CelestialObject() {
            private State state= new State(0.0, new double[]{1.5e11,0,0,0,30000,0});
            @Override
            public String getName() {
                return "Earth";
            }

            @Override
            public double getMass() {
                return 5.972e24;
            }

            @Override
            public State getState() {
                return state;
            }

            @Override
            public boolean isSpaceship() {
                return false;
            }
            public boolean isProbe(){
                return false;
            }

            @Override
            public void setState(State s) {
            this.state = s;
            }

            @Override
            public void setMass(double mass) {

            }

            @Override
            public void setScale(double scale) {

            }

            @Override
            public void moveCelestialObject(double[] position) {

            }
        };


        bodies.add(sun);
        bodies.add(earth);

        SolarSystemEngine engine = new SolarSystemEngine(bodies);

        double[] sunPosBefore = sun.getState().getPos().clone();
        double[] earthPosBefore = earth.getState().getPos().clone();
        double[] earthBefore = earth.getState().getVel().clone();
        //store velocities

        engine.evolve(0.0,86400.0);

        double[] sunPosAfter =sun.getState().getPos();
        double[] earthPosAfter = earth.getState().getPos();
        double[] earthVelAfter = earth.getState().getVel();
        //store updated velocities

        assertEquals(0, sunPosAfter[0], 1e-10, "Sun X position should be 0");
        assertEquals(0, sunPosAfter[1], 1e-10, "Sun Y position should be 0");
        assertEquals(0, sunPosAfter[2], 1e-10, "Sun Z position should be 0");

        assertTrue(Math.abs(earthPosBefore[0] - earthPosAfter[0]) > 1e-3, " Earth X position should have changed");
        assertTrue(Math.abs(earthPosBefore[1] - earthPosAfter[1])> 1e-3, " Earth Y position should have changed ");

        assertTrue(Math.abs(earthPosAfter[1]) > 1e9, " Earth should have moved significantly in Y direction");

        //check distance before+ after evaluation
        double distanceBefore = Math.sqrt(
                        earthPosBefore[0] * earthPosBefore[0] +
                        earthPosBefore[1] * earthPosBefore[1] +
                        earthPosBefore[2] * earthPosBefore[2]
        );

        double distanceAfter = Math.sqrt(
                        earthPosAfter[0] * earthPosAfter[0] +
                        earthPosAfter[1] * earthPosAfter[1] +
                        earthPosAfter[2] * earthPosAfter[2]
        );

        assertEquals(distanceBefore, distanceAfter, distanceBefore * 0.01, " Earth's distance from Sun should remain roughly constant");


    }
}

