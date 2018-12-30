package org.twz.dag.actor;

import org.twz.dag.Gene;
import org.twz.prob.ISampler;

import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class Sampler implements ISampler {
    private SimulationActor Actor;
    private Gene Loc;

    public Sampler(SimulationActor actor, Gene loc) {
        Actor = actor;
        Loc = loc;
    }

    public double next() {
        return Actor.sample(Loc);
    }

    public double next(Map<String, Double> exo) {
        return Actor.sample(Loc, exo);
    }

    @Override
    public String getName() {
        return Actor.Field;
    }

    @Override
    public double sample() {
        return next();
    }

    public double sample(Map<String, Double> exo) {
        return next(exo);
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

    public double[] sample(int n, Map<String, Double> exo) {
        n = Math.max(n, 1);
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = next(exo);
        }
        return res;
    }

    @Override
    public String toString() {
        return Actor.toString();
    }
}
