package org.twz.dag.actor;

import org.twz.dag.Gene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public abstract class SimulationActor {
    public final String Field;

    SimulationActor(String field) {
        Field = field;
    }

    protected abstract List<String> getParents();

    public abstract double sample(Gene pas);

    public abstract double sample(Gene pas, Map<String, Double> exo);

    protected Map<String, Double> findParentValues(Gene gene) {
        Map<String, Double> ps = new HashMap<>();
        for (String s : getParents()) {
            ps.put(s, gene.get(s));
        }
        return ps;
    }

    protected Map<String, Double> findParentValues(Gene gene, Map<String, Double> exo) {
        Map<String, Double> ps = new HashMap<>();
        for (String s : getParents()) {
            ps.put(s, exo.containsKey(s)? exo.get(s): gene.get(s));
        }
        return ps;
    }

}
