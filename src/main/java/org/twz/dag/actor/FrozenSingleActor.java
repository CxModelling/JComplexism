package org.twz.dag.actor;

import org.twz.dag.Chromosome;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.Loci;
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.IDistribution;
import org.twz.prob.IWalkable;

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
        try {
            Distribution = Loci.findDistribution(pas);
        } catch (IncompleteConditionException e) {
            e.printStackTrace();
        }
    }

    public FrozenSingleActor(String field, Loci di, Chromosome pas) {
        super(field);
        Loci = (DistributionLoci) di;
        try {
            Distribution = Loci.findDistribution(pas);
        } catch (IncompleteConditionException e) {
            e.printStackTrace();
        }
    }

    public void update(Chromosome chromosome) throws IncompleteConditionException {
       Distribution = Loci.findDistribution(chromosome);
    }

    public void update(Chromosome chromosome, Map<String, Double> exo) {
        try {
            Distribution = Loci.findDistribution(findParentValues(chromosome, exo));
        } catch (IncompleteConditionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<String> getParents() {
        return Loci.getParents();
    }

    @Override
    public double sample(Chromosome pas) throws IncompleteConditionException {
        return Distribution.sample();
    }

    @Override
    public double sample(Chromosome pas, Map<String, Double> exo) throws IncompleteConditionException {
        return Distribution.sample();
    }

    @Override
    public String toString() {
        return Distribution.getName();
    }
}
