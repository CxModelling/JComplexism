package org.twz.fit.genetic;

import org.twz.dag.Gene;
import org.twz.misc.Statistics;
import org.twz.prob.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImportanceSelection extends AbsSelection {
    @Override
    public List<Gene> select(List<Gene> population, String target) {
        int n = population.size();
        List<Gene> fil = population.stream().filter(g-> Double.isFinite(g.getLogLikelihood()))
                .filter(g->Double.isFinite(g.getLogPriorProb())).collect(Collectors.toList());

        double[] wts;
        if (target.equals("MLE")) {
            wts = getLikelihoodArray(fil);
        } else {
            wts = getPosteriorArray(fil);
        }
        wts = Statistics.logNormalise(wts);
        wts = Statistics.exp(wts);
        return resample(fil, wts, n);
    }

    private double[] getLikelihoodArray(List<Gene> population) {
        return population.stream().mapToDouble(Gene::getLogLikelihood).toArray();
    }

    private double[] getPosteriorArray(List<Gene> population) {
        return population.stream().mapToDouble(Gene::getLogPosterior).toArray();
    }

    private List<Gene> resample(List<Gene> ps, double[] wts, int n) {
        List<Gene> post = new ArrayList<>();
        for (int i : Sample.sample(wts, n)) {
            post.add(ps.get(i).clone());
        }
        return post;
    }
}
