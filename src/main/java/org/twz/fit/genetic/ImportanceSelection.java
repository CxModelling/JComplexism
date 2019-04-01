package org.twz.fit.genetic;

import org.twz.dag.Chromosome;
import org.twz.util.Statistics;
import org.twz.prob.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImportanceSelection extends AbsSelection {
    @Override
    public List<Chromosome> select(List<Chromosome> population, String target) {
        int n = population.size();
        List<Chromosome> fil = population.stream().filter(g-> Double.isFinite(g.getLogLikelihood()))
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

    private double[] getLikelihoodArray(List<Chromosome> population) {
        return population.stream().mapToDouble(Chromosome::getLogLikelihood).toArray();
    }

    private double[] getPosteriorArray(List<Chromosome> population) {
        return population.stream().mapToDouble(Chromosome::getLogPosterior).toArray();
    }

    private List<Chromosome> resample(List<Chromosome> ps, double[] wts, int n) {
        List<Chromosome> post = new ArrayList<>();
        for (int i : Sample.sample(wts, n)) {
            post.add(ps.get(i).clone());
        }
        return post;
    }
}
