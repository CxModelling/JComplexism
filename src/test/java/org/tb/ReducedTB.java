package org.tb;


import org.twz.dag.BayesNet;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;

public class ReducedTB extends BayesianModel {

    public ReducedTB(BayesNet bn) {
        super(bn);
    }

    @Override
    public Gene samplePrior() {
        return null;
    }

    @Override
    public void evaluateLogLikelihood(Gene gene) {

    }

    @Override
    public boolean hasExactLikelihood() {
        return false;
    }


}
