package org.twz.fit.genetic;

import org.twz.dag.Chromosome;
import org.twz.prob.Sample;

import java.util.ArrayList;
import java.util.List;

public class TournamentSelection extends AbsSelection {
    @Override
    public List<Chromosome> select(List<Chromosome> population, String target) {
        List<Chromosome> picked = new ArrayList<>();
        int n = population.size();

        Chromosome g1, g2;
        while(picked.size() < n) {
            g1 = population.get(Sample.sampleN(n));
            g2 = population.get(Sample.sampleN(n));
            picked.add(match(g1, g2, target));
        }
        return picked;
    }

    private Chromosome match(Chromosome g1, Chromosome g2, String target) {
        Chromosome winner;
        if (target.equals("MLE")) {
            winner = (g1.getLogLikelihood()>g2.getLogLikelihood())?g1:g2;
        } else {
            winner = (g1.getLogPosterior()>g2.getLogPosterior())?g1:g2;
        }
        return winner.clone();
    }
}
