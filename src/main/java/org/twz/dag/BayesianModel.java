package org.twz.dag;

import org.twz.prob.IDistribution;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.Loci;
import org.twz.statistic.Statistics;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public class BayesianModel {

    private static int DefaultSizeMC = 100;

    private final BayesNet BN;

    public BayesianModel(BayesNet bn) {
        BN = bn;
    }

    public Map<String, IDistribution> sampleDistribution() {
        return null;
    }

    public Gene samplePrior() {
        return null;
    }

    public double evaluatePrior(Gene gene) {
        return 0;
    }

    public double evaluateLikelihood(Gene gene) {
        return 0;
    }


    public void print() {
        System.out.println("DAG:");
        BN.print();
    }


}
