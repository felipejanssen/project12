package PhysicsJUnitTests;

import Backend.Physics.Impulse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImpulseJUnitTest {

    @Test
    public void testConstrcutorWithImpulseVector(){
        //impulse+time vector initialize
        double[] vector = {3.0, 4.0, 0.0};
        double time = 5.0;

        //creating obj with the vectors
        Impulse impulse = new Impulse(vector, time);

        //veryfying time+magnitude+impulse
        assertArrayEquals(vector, impulse.getImpulseVec());
        assertEquals(time, impulse.getTime(), 1e-6);
        assertEquals(5.0, impulse.getMag(), 1e-6);
    }

    @Test
    public void testChangeImpulse(){
        double[] vector = {1.0, 2.0, 3.0};
        Impulse impulse = new Impulse(vector, 0.0);

        //changing impulse + getting the modified versiom
        impulse.changeImpulse(1, 5.0);
        double[] modified =impulse.getImpulseVec();

        //verifying
        assertEquals(7.0, modified[1], 1e-6);
        assertEquals(1.0, modified[0], 1e-6);
        assertEquals(3.0, modified[2], 1e-6);
    }

    @Test
    public void testGetNormalizedDir(){
        double[] vector = {3.0, 0.0, 4.0};
        Impulse impulse = new Impulse(vector.clone(), 0.0);

        double[] norm = impulse.getNormalizedDir();
        double magnitude = Math.sqrt(3*3 + 4*4);

        //verifying normalization of the direction
        assertEquals(3.0 /magnitude, norm[0], 1e-6);
        assertEquals(0.0, norm[1], 1e-6);
        assertEquals(4.0 /magnitude, norm[2], 1e-6);
    }

    @Test
    public void testClone(){
        double[] vector = {2.0, 2.0, 1.0};
        Impulse impulse = new Impulse(vector.clone(), 1.0);
        Impulse clone = impulse.clone();

        //verifying that the cloned obj is not the original
        assertNotSame(impulse, clone);
        assertArrayEquals(impulse.getImpulseVec(), clone.getImpulseVec());
        assertEquals(impulse.getTime(), clone.getTime(),1e-6);
    }

    @Test
    public void testChangeMagBy(){
        double[] vector = {3.0, 4.0, 0.0};
        Impulse impulse = new Impulse(vector.clone(), 0.0);
        impulse.changeMagBy(10.0);
        //veifying that magnitude was changed
        assertEquals(10.0, impulse.changeMagBy(0.0), 1e-6);
    }
}
