package org.twz.dag.actor;


import org.twz.cx.element.Disclosure;
import org.twz.dag.Gene;
import org.twz.dag.loci.Loci;

import java.util.HashMap;
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
    public double sample(Gene pas) {
        return Distribution.sample(pas);
    }

    @Override
    public double sample(Gene pas, Map<String, Double> exo) {
        Map<String, Double> ps = new HashMap<>();
        for (String p :Distribution.getParents()) {
            try {
                ps.put(p, pas.get(p));
            } catch (NullPointerException e) {
                ps.put(p, exo.get(p));
            }
        }
        return Distribution.sample(ps);
    }

    @Override
    public void fill(Gene pas) {
        Distribution.fill(pas);
    }

    @Override
    public void fill(Gene pas, Map<String, Double> exo) {

    }

    @Override
    public String toString() {
        return Distribution.getDefinition();
    }
}
