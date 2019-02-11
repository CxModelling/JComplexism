package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.prob.Sample;
import org.twz.misc.Statistics;
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
    public List<Gene> fit(BayesianModel bm) {
        info("Initialising");
        int n = getOptionInteger("N_post");
        List<Gene> prior = sampling(bm, n);
        double[] imp = importance(bm, prior);
        return resampling(bm, prior, imp, n);
    }

    @Override
    public List<Gene> update(BayesianModel bm) {
        error("No update methods for Sampling-Important-Resampling");
        return null;
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }

    private List<Gene> sampling(BayesianModel bm, int n) {
        info("Sampling");

        List<Gene> prior = new ArrayList<>();

        try {
            for (Gene gene : (bm.getPriorSample())) {
                prior.add(gene);
                if (prior.size() >= n) {
                    break;
                }
            }
        } catch (AssertionError ignored) {

        }
        appendPriorUntil(bm, n, prior);
        return prior;
    }

    private double[] importance(BayesianModel bm, List<Gene> prior) {
        info("Calculating importance");
        int n = prior.size();
        double[] lis = new double[n];
        for (int i = 0; i < n; i++) {
            Gene gene = prior.get(i);
            if (!gene.isPriorEvaluated()) bm.evaluateLogPrior(gene);
            if (!gene.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(gene);
            lis[i] = gene.getLogLikelihood();
        }
        lis = Statistics.add(lis, -Statistics.lse(lis));
        lis = Statistics.exp(lis);
        return lis;
    }

    private List<Gene> resampling(BayesianModel bm, List<Gene> prior, double[] lis, int n) {
        info("Resampling");

        List<Gene> post = new ArrayList<>();
        for (int i: Sample.sample(lis, n)) {
            Gene gene = prior.get(i);
            post.add(prior.get(i));
            bm.keepMemento(gene, "Posterior");
        }
        info("Finished");
        return post;
    }

}
