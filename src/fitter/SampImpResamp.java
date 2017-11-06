package fitter;

import pcore.BayesianModel;
import pcore.Gene;
import utils.Sample;
import utils.Statistics;
import java.util.*;



/**
 * Sampling importance resampling
 * Created by TimeWz on 2017/4/25.
 */
public class SampImpResamp implements IFitter {
    private BayesianModel Model;
    private List<Gene> Prior, Posterior;

    public SampImpResamp(BayesianModel model) {
        Model = model;
        Prior = new ArrayList<>();
        Posterior = new ArrayList<>();

    }

    @Override
    public List<Gene> getPosterior() {
        return Posterior;
    }

    @Override
    public void fit(int niter) {
        Prior.clear();
        Posterior.clear();

        Gene g;
        double li;
        double[] lis = new double[niter];
        for (int i = 0; i < niter; i++) {
            g = Model.samplePrior();
            li = Model.evaluateLikelihood(g);
            g.setLogLikelihood(li);
            lis[i] = li;
            Prior.add(g);
        }
        lis = Statistics.add(lis, -Statistics.lse(lis));
        lis = Statistics.exp(lis);

        for (int i: Sample.sample(lis, niter)) {
            Posterior.add(Prior.get(i));
        }
    }

}
