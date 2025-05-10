package Backend.Optimizers;

import java.util.ArrayList;
import java.util.List;

import Backend.Physics.Impulse;

public class Optimizer {

    public static void main(String[] args) {
        List<Impulse> initImpulses = new ArrayList<>();
        Impulse launchImpulse = new Impulse(new double[] { 1, 3, .2 }, 0);
        initImpulses.add(launchImpulse);

        GradientDescentOpt opt = new GradientDescentOpt();
        List<Impulse> optimizedImpulses = opt.optimize(initImpulses);
    }
}