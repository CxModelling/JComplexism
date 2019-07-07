package org.twz.dag.actor;

import org.twz.dag.BayesNet;
import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public abstract double sample(Chromosome pas) throws IncompleteConditionException;

    public abstract double sample(Chromosome pas, Map<String, Double> exo) throws IncompleteConditionException;

    protected Map<String, Double> findParentValues(Chromosome chromosome) {
        Map<String, Double> ps = new HashMap<>();
        for (String s : getParents()) {
            ps.put(s, chromosome.getDouble(s));
        }
        return ps;
    }

    protected Map<String, Double> findParentValues(Chromosome chromosome, Map<String, Double> exo) {
        Map<String, Double> ps = new HashMap<>();
        for (String s : getParents()) {
            ps.put(s, exo.containsKey(s)? exo.get(s): chromosome.getDouble(s));
        }
        return ps;
    }

    public List<String> getRequirement(BayesNet bn) {
        return bn.getDAG().getMinimalRequirement(Field, getParents());
    }

}
