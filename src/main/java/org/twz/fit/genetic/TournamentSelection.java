package org.twz.fit.genetic;

import org.twz.dag.Gene;
import org.twz.prob.Sample;

import java.util.ArrayList;
import java.util.List;

public class TournamentSelection extends AbsSelection {
    @Override
    public List<Gene> select(List<Gene> population, String target) {
        List<Gene> picked = new ArrayList<>();
        int n = population.size();

        Gene g1, g2;
        while(picked.size() < n) {
            g1 = population.get(Sample.sampleN(n));
            g2 = population.get(Sample.sampleN(n));
            picked.add(match(g1, g2, target));
        }
        return picked;
    }

    private Gene match(Gene g1, Gene g2, String target) {
        Gene winner;
        if (target.equals("MLE")) {
            winner = (g1.getLogLikelihood()>g2.getLogLikelihood())?g1:g2;
        } else {
            winner = (g1.getLogPosterior()>g2.getLogPosterior())?g1:g2;
        }
        return winner.clone();
    }
}
