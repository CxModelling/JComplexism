package org.twz.dag.actor;


import org.twz.dag.loci.Loci;
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
    public double sample(Map<String, Double> pas) {
        return Distribution.sample(pas);
    }

    @Override
    public String toString() {
        return Field + " (" + Distribution.getDefinition() + ")";
    }
}
