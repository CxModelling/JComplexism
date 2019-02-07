package org.twz.fit;

import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.misc.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ABC extends BayesianFitter {

    private double Epsilon;

    public ABC(BayesianModel model) {
        super(model);
        Options.put("N_test", 100.0);
        Options.put("P_test", 0.1);
        Epsilon = Double.NaN;
    }

    public void setEpsilon(double eps) {
        Epsilon = eps;
        Posterior.clear();
        info("Reset epsilon to " + Epsilon);
        info("Posterior erased");
    }

    @Override
    public void update(int niter) {
        info("Collecting posterior");
        Gene x;
        int i = 0;

        while(i < niter) {
            x = Model.samplePrior();
            Model.evaluateLogLikelihood(x);
            if (x.getLogLikelihood() > Epsilon) {
                Posterior.add(x);
                i ++;
                if (i % (niter /4) == 0) {
                    info("Progress: " + 100*i/niter + "%");
                }
            }
        }
        info("Finished");
    }

    @Override
    public void fit(int niter) {
        if (Double.isNaN(Epsilon)) {
            test();
        }
        update(niter);
    }

    @Override
    public Map<String, Double> getGoodnessOfFit() {
        return null;
    }

    private void test() {
        info("Testing sample");
        int n = Options.get("N_test").intValue();
        double p = Options.get("P_test");

        List<Gene> xs = new ArrayList<>();
        for (Gene gene : getPrior()) {
            xs.add(gene);
            if (xs.size() >= n) {
                break;
            }
        }

        while(xs.size() < n) {
            xs.add(Model.samplePrior());
        }

        double[] lis = new double[n];
        for (int i = 0; i < n; i++) {
            Gene x = xs.get(i);
            if (!x.isLikelihoodEvaluated()) {
                Model.evaluateLogLikelihood(x);
            }
            lis[i] = x.getLogLikelihood();
        }
        Epsilon = Statistics.quantile(lis, 1-p);
        info("Test sample suggest epi=" + Epsilon);
    }
}
