package org.twz.dag.actor;

import org.twz.dag.Chromosome;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.Loci;
import org.twz.prob.IWalkable;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class FrozenSingleActor extends SimulationActor {
    private IWalkable Distribution;
    private DistributionLoci Loci;

    public FrozenSingleActor(String field, Loci di, Map<String, Double> pas) {
        super(field);
        Loci = (DistributionLoci) di;
        Distribution = Loci.findDistribution(pas);
    }

    public FrozenSingleActor(String field, Loci di, Chromosome pas) {
        super(field);
        Loci = (DistributionLoci) di;
        Distribution = Loci.findDistribution(pas);
    }

    public void update(Chromosome chromosome) {
       Distribution = Loci.findDistribution(chromosome);
    }

    public void update(Chromosome chromosome, Map<String, Double> exo) {
        Distribution = Loci.findDistribution(findParentValues(chromosome, exo));
    }

    @Override
    protected List<String> getParents() {
        return Loci.getParents();
    }

    @Override
    public double sample(Chromosome pas) {
        return Distribution.sample();
    }

    @Override
    public double sample(Chromosome pas, Map<String, Double> exo) {
        return Distribution.sample();
    }

    @Override
    public String toString() {
        return Distribution.getName();
    }
}
