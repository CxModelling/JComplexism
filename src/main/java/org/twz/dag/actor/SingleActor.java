package org.twz.dag.actor;


import org.twz.dag.Gene;
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
    public double sample(Gene pas) {
        return Distribution.sample(pas);
    }

    @Override
    public void fill(Gene pas) {
        Distribution.fill(pas);
    }

    @Override
    public String toString() {
        return Distribution.getDefinition();
    }
}
