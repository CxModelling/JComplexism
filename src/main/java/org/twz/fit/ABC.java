package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;
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
    public List<Chromosome> fit(BayesianModel bm) {
        if (Double.isNaN(Epsilon)) {
            test(bm);
        }
        return collectPosterior(bm, getOptionInteger("N_post"), "Posterior");
    }

    @Override
    public List<Chromosome> update(BayesianModel bm) {
        return collectPosterior(bm, getOptionInteger("N_update"), "Update");
    }

    @Override
    public OutputSummary getSummary(BayesianModel bm) {
        return getSummary(bm, false);
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }

    private void test(BayesianModel bm) {
        info("Testing render");
        int n = getOptionInteger ("N_test");
        double p = getOptionDouble("P_test");

        List<Chromosome> xs = new ArrayList<>();

        try {
            for (Chromosome chromosome : (bm.getPriorSample())) {
                xs.add(chromosome);
                if (xs.size() >= n) {
                    break;
                }
            }
        } catch (AssertionError ignored) {

        }

        appendPriorUntil(bm, n, xs);

        double[] lis = xs.stream()
                .mapToDouble(Chromosome::getLogLikelihood).toArray();

        Epsilon = Statistics.quantile(lis, 1-p);
        info("Test render suggest epi=" + Epsilon);
    }

    private List<Chromosome> collectPosterior(BayesianModel bm, int n, String tag) {
        List<Chromosome> res = new ArrayList<>();
        int max_iter = (int) (n / getOptionDouble("P_min_acc"));

        int count = 0;
        while (res.size() < n) {
            count ++;
            Chromosome chromosome = bm.samplePrior();
            if (!chromosome.isPriorEvaluated()) bm.evaluateLogPrior(chromosome);
            if (!chromosome.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(chromosome);
            if (chromosome.getLogLikelihood() > Epsilon) {
                res.add(chromosome);
                bm.keepMemento(chromosome, tag);
            }

            if (count > max_iter) {
                warning("Acceptance is small them " + getOptionDouble("P_min_acc"));
            }
        }
        return res;
    }
}
