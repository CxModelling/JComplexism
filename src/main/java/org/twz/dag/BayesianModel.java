package org.twz.dag;

import org.twz.fit.ValueDomain;
import org.twz.prob.IDistribution;
import org.twz.dag.loci.DistributionLoci;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public abstract class BayesianModel {

    private final BayesNet BN;

    public BayesianModel(BayesNet bn) {
        BN = bn;
    }

    public List<ValueDomain> getMovableNodes() {
        Gene p = samplePrior();

        List<ValueDomain> res = new ArrayList<>();
        DistributionLoci loci;
        for (String s : BN.getRVRoots()) {
            loci = (DistributionLoci) BN.getLoci(s);
            IDistribution d = loci.findDistribution(p);
            res.add(new ValueDomain(s, d.getDataType(), d.getLower(), d.getUpper()));
        }
        return res;
    }

    public abstract Gene samplePrior();

    public void evaluateLogPrior(Gene gene) {
        BN.evaluate(gene);
    }

    public abstract void evaluateLogLikelihood(Gene gene);

    public abstract boolean hasExactLikelihood();

    public void print() {
        System.out.println("DAG:");
        BN.print();
    }


}
