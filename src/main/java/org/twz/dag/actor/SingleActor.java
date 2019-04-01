package org.twz.dag.actor;


import org.twz.dag.Chromosome;
import org.twz.dag.loci.Loci;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class SingleActor extends SimulationActor {
    private Loci Distribution;


    public SingleActor(String field, Loci loci) {
        super(field);
        Distribution = loci;
    }

    @Override
    protected List<String> getParents() {
        return Distribution.getParents();
    }

    @Override
    public double sample(Chromosome pas) {
        try {
            return Distribution.render(pas);
        } catch (org.twz.exception.IncompleteConditionException e) {
            e.printStackTrace();
        }
        return Double.NaN;
    }

    @Override
    public double sample(Chromosome pas, Map<String, Double> exo) {
        try {
            return Distribution.render(findParentValues(pas, exo));
        } catch (org.twz.exception.IncompleteConditionException e) {
            e.printStackTrace();
        }
        return Double.NaN;
    }

    @Override
    public String toString() {
        return Distribution.getDefinition();
    }
}
