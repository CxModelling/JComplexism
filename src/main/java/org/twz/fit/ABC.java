package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.util.Statistics;

import java.util.ArrayList;
import java.util.List;

public class ABC extends BayesianFitter {

    private double Epsilon;

    public ABC(int n_post) {
        super();
        Options.put("N_test", 100);
        Options.put("P_test", 0.1);
        Options.put("N_post", n_post);
        Options.put("N_update", 500);
        Options.put("P_min_acc", 0.001);
        Epsilon = Double.NaN;
    }

    public ABC(int n_post, double min_acc) {
        this(n_post);
        setOption("P_min_acc", min_acc);
    }

    public void setEpsilon(double eps) {
        Epsilon = eps;
        info("Reset epsilon to " + Epsilon);
    }


    @Override
    public List<Gene> fit(BayesianModel bm) {
        if (Double.isNaN(Epsilon)) {
            test(bm);
        }
        return collectPosterior(bm, getOptionInteger("N_post"), "Posterior");
    }

    @Override
    public List<Gene> update(BayesianModel bm) {
        return collectPosterior(bm, getOptionInteger("N_update"), "Update");
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }

    private void test(BayesianModel bm) {
        info("Testing sample");
        int n = getOptionInteger ("N_test");
        double p = getOptionDouble("P_test");

        List<Gene> xs = new ArrayList<>();

        try {
            for (Gene gene : (bm.getPriorSample())) {
                xs.add(gene);
                if (xs.size() >= n) {
                    break;
                }
            }
        } catch (AssertionError ignored) {

        }

        appendPriorUntil(bm, n, xs);

        double[] lis = xs.stream()
                .mapToDouble(Gene::getLogLikelihood).toArray();

        Epsilon = Statistics.quantile(lis, 1-p);
        info("Test sample suggest epi=" + Epsilon);
    }

    private List<Gene> collectPosterior(BayesianModel bm, int n, String tag) {
        List<Gene> res = new ArrayList<>();
        int max_iter = (int) (n / getOptionDouble("P_min_acc"));

        int count = 0;
        while (res.size() < n) {
            count ++;
            Gene gene = bm.samplePrior();
            if (!gene.isPriorEvaluated()) bm.evaluateLogPrior(gene);
            if (!gene.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(gene);
            if (gene.getLogLikelihood() > Epsilon) {
                res.add(gene);
                bm.keepMemento(gene, tag);
            }

            if (count > max_iter) {
                warning("Acceptance is small them " + getOptionDouble("P_min_acc"));
            }
        }
        return res;
    }
}
