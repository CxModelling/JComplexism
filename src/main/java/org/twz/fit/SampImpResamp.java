package org.twz.fit;

import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.prob.Sample;
import org.twz.statistic.Statistics;
import java.util.*;



/**
 * Sampling importance resampling
 * Created by TimeWz on 2017/4/25.
 */
public class SampImpResamp extends BayesianFitter {
    private BayesianModel Model;

    public SampImpResamp(BayesianModel model) {
        super(model);
    }

    @Override
    public List<Gene> getPosterior() {
        return Posterior;
    }

    @Override
    public void fit(int niter) {
        info("Initialising");

        List<Gene> prior = new ArrayList<>();
        info("Sampling");
        if (getPrior().size() >= niter) {
            for (int i = 0; i < niter; i++) {
                prior.add(getPrior().get(i));
            }
        } else {
            for (int i = 0; i < niter; i++) {
                prior.add(Model.samplePrior());
            }
        }

        info("Calculating importance");
        Gene g;
        double li;
        double[] lis = new double[niter];
        for (int i = 0; i < niter; i++) {
            g = prior.get(i);
            if (g.isEvaluated()) {
                lis[i] = g.getLogLikelihood();
            } else {
                li = Model.evaluateLogLikelihood(g);
                g.setLogLikelihood(li);
            }
        }
        lis = Statistics.add(lis, -Statistics.lse(lis));
        lis = Statistics.exp(lis);

        Posterior = new ArrayList<>();
        for (int i: Sample.sample(lis, niter)) {
            Posterior.add(prior.get(i));
        }
    }

    @Override
    public void summarisePrior() {
        // todo
    }

    @Override
    public void summarisePosterior() {
        // todo
    }

    @Override
    public Map<String, Double> getGoodnessOfFit() {
        // todo
        return null;
    }

}
