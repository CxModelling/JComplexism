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

    public void update() {
        if (Actor instanceof FrozenSingleActor) {
            try {
                ((FrozenSingleActor) Actor).update(Loc);
            } catch (IncompleteConditionException e) {
                e.printStackTrace();
            }
        }
    }

    public double next() {
        try {
            return Actor.sample(Loc);
        } catch (IncompleteConditionException | NullPointerException e) {
            return Double.NaN;
        }
    }

    public double next(Map<String, Double> exo) {
        try {
            return Actor.sample(Loc, exo);
        } catch (IncompleteConditionException e) {
            return Double.NaN;
        }
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
    public double sample() {
        return next();
    }

    public double sample(Map<String, Double> exo) throws IncompleteConditionException {
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

    public double[] sample(int n, Map<String, Double> exo) throws IncompleteConditionException {
        n = Math.max(n, 1);
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = next(exo);
        }
        return res;
    }

    public boolean isFrozen() {
        return Actor instanceof FrozenSingleActor;
    }

    @Override
    public String toString() {
        return Actor.toString();
    }
}
