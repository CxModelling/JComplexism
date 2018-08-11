package org.twz.dag.actor;

import org.twz.dag.Gene;
import org.twz.prob.ISampler;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class Sampler implements ISampler {
    SimulationActor Actor;
    Gene Loc;

    public Sampler(SimulationActor actor, Gene loc) {
        Actor = actor;
        Loc = loc;
    }



    public double next() {
        return Actor.sample(Loc);
    }

    @Override
    public String getName() {
        return Actor.Field;
    }

    @Override
    public double sample() {
        return next();
    }

    @Override
    public double[] sample(int n) {
        n = Math.max(n, 1);
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = next();
        }
        return res;
    }

    @Override
    public String toString() {
        return Actor.toString();
    }
}
