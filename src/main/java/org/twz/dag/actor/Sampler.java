package org.twz.dag.actor;

import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.IDistribution;

import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class Sampler implements IDistribution {
    private SimulationActor Actor;
    private Chromosome Loc;

    public Sampler(SimulationActor actor, Chromosome loc) {
        Actor = actor;
        Loc = loc;
    }

    public double next() throws IncompleteConditionException {
        return Actor.sample(Loc);
    }

    public double next(Map<String, Double> exo) throws IncompleteConditionException {
        return Actor.sample(Loc, exo);
    }

    @Override
    public String getName() {
        return Actor.Field;
    }

    @Override
    public String getDataType() {
        return Actor.Field;
    }

    @Override
    public double logProb(double rv) {
        return 0;
    }

    @Override
    public double sample() throws IncompleteConditionException {
        return next();
    }

    public double sample(Map<String, Double> exo) throws IncompleteConditionException {
        return next(exo);
    }

    @Override
    public double[] sample(int n) throws IncompleteConditionException {
        n = Math.max(n, 1);
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = next();
        }
        return res;
    }

    public double[] sample(int n, Map<String, Double> exo) throws IncompleteConditionException {
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
