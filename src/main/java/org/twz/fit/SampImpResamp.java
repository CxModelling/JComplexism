package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;
import org.twz.prob.Sample;
import org.twz.util.Statistics;
import java.util.*;



/**
 * Sampling importance resampling
 * Created by TimeWz on 2017/4/25.
 */
public class SampImpResamp extends BayesianFitter {
    public SampImpResamp(int n_post) {
        super();
        setOption("N_post", n_post);
    }

    @Override
    public List<Chromosome> fit(BayesianModel bm) {
        info("Initialising");
        int n = getOptionInteger("N_post");
        List<Chromosome> prior = sampling(bm, n);
        double[] imp = importance(bm, prior);
        return resampling(bm, prior, imp, n);
    }

    @Override
    public List<Chromosome> update(BayesianModel bm) {
        error("No update methods for Sampling-Important-Resampling");
        return null;
    }

    @Override
    public OutputSummary getSummary(BayesianModel bm) {
        return getSummary(bm, false);
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }

    private List<Chromosome> sampling(BayesianModel bm, int n) {
        info("Sampling");

        List<Chromosome> prior = new ArrayList<>();

        try {
            for (Chromosome chromosome : (bm.getPriorSample())) {
                prior.add(chromosome);
                if (prior.size() >= n) {
                    break;
                }
            }
        } catch (AssertionError ignored) {

        }
        appendPriorUntil(bm, n, prior);
        return prior;
    }

    private double[] importance(BayesianModel bm, List<Chromosome> prior) {
        info("Calculating importance");
        int n = prior.size();
        double[] lis = new double[n];
        for (int i = 0; i < n; i++) {
            Chromosome chromosome = prior.get(i);
            if (!chromosome.isPriorEvaluated()) bm.evaluateLogPrior(chromosome);
            if (!chromosome.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(chromosome);
            lis[i] = chromosome.getLogLikelihood();
        }
        lis = Statistics.add(lis, -Statistics.lse(lis));
        lis = Statistics.exp(lis);
        return lis;
    }

    private List<Chromosome> resampling(BayesianModel bm, List<Chromosome> prior, double[] lis, int n) {
        info("Resampling");

        List<Chromosome> post = new ArrayList<>();
        for (int i: Sample.sample(lis, n)) {
            Chromosome chromosome = prior.get(i);
            post.add(prior.get(i));
            bm.keepMemento(chromosome, "Posterior");
        }
        info("Finished");
        return post;
    }

}
