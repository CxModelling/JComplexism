package org.twz.dag.actor;

import org.twz.dag.Gene;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TimeWz on 08/08/2018.
 */
public class Sampler {
    SimulationActor Actor;
    Gene Loc;

    public Sampler(SimulationActor actor, Gene loc) {
        Actor = actor;
        Loc = loc;
    }

    public double next() {
        return Actor.sample(Loc);
    }

    public double[] sample(int n) {
        n = Math.max(n, 1);
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = next();
        }
        return res;
    }
}
