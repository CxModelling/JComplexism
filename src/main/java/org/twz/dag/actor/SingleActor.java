package org.twz.dag.actor;


import org.twz.cx.element.Disclosure;
import org.twz.dag.Gene;
import org.twz.dag.loci.Loci;

import java.util.HashMap;
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
    public double sample(Gene pas) {
        return Distribution.sample(pas);
    }

    @Override
    public double sample(Gene pas, Map<String, Double> exo) {
        return Distribution.sample(findParentValues(pas, exo));
    }

    @Override
    public String toString() {
        return Distribution.getDefinition();
    }
}
