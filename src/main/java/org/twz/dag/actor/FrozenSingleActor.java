package org.twz.dag.actor;

import org.twz.dag.Gene;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.Loci;
import org.twz.prob.IDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class FrozenSingleActor extends SimulationActor {
    private IDistribution Distribution;
    private DistributionLoci Loci;

    public FrozenSingleActor(String field, Loci di, Map<String, Double> pas) {
        super(field);
        Loci = (DistributionLoci) di;
        Distribution = Loci.findDistribution(pas);
    }

    public FrozenSingleActor(String field, Loci di, Gene pas) {
        super(field);
        Loci = (DistributionLoci) di;
        Distribution = Loci.findDistribution(pas);
    }

    public void update(Gene gene) {
       Distribution = Loci.findDistribution(gene);
    }

    public void update(Gene gene, Map<String, Double> exo) {
        Distribution = Loci.findDistribution(findParentValues(gene, exo));
    }

    @Override
    protected List<String> getParents() {
        return Loci.getParents();
    }

    @Override
    public double sample(Gene pas) {
        return Distribution.sample();
    }

    @Override
    public double sample(Gene pas, Map<String, Double> exo) {
        return Distribution.sample();
    }

    @Override
    public String toString() {
        return Distribution.getName();
    }
}
