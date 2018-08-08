package org.twz.dag.actor;

import org.twz.dag.ScriptException;

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

    public abstract double sample(Map<String, Double> pas);
}
