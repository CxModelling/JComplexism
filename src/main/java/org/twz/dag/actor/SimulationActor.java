package org.twz.dag.actor;

import org.twz.dag.Gene;

import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public abstract class SimulationActor {
    protected final String Field;

    public SimulationActor(String field) {
        Field = field;
    }

    public abstract double sample(Gene pas);

    public abstract double sample(Gene pas, Map<String, Double> exo);

    public abstract void fill(Gene pas);

    public abstract void fill(Gene pas, Map<String, Double> exo);

    public void update(Map<String, Double> pas) {}

    public void update(Gene gene) {}

}
