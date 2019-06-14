package org.twz.fit;

import org.junit.Before;
import org.junit.Test;
import org.twz.dag.BayesNet;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;
import org.twz.prob.Binom;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MCMCTest {

    private BayesianModel BM;

    @Before
    public void setUp() throws Exception {
        BayesNet bn = new BayesNet("m");
        bn.appendLoci("p ~ beta(1, 1)");


        BM = new BayesianModel(bn) {
            @Override
            public Chromosome samplePrior() {
                return BN.sample();
            }

            @Override
            public void evaluateLogLikelihood(Chromosome chromosome) {
                double li = (new Binom(100, chromosome.getDouble("p"))).logProb(30);
                chromosome.setLogLikelihood(li);
            }

            @Override
            public boolean hasExactLikelihood() {
                return true;
            }

            @Override
            public void keepMemento(Chromosome chromosome, String type) {

            }

            @Override
            public void clearMementos(String type) {

            }

            @Override
            public void clearMementos() {

            }
        };
    }

    @Test
    public void fit() {
        MCMC fitter = new MCMC(1000, BM.getMovableNodes());
        fitter.setOption("N_burn_in", 3000);
        fitter.onLog();
        BM.fit(fitter);

        fitter.getSummary(BM).println();
    }

    @Test
    public void fitsir() {
        SampImpResamp fitter = new SampImpResamp(1000);

        BM.fit(fitter);
        fitter.getSummary(BM).println();
    }
}