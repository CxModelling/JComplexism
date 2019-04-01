package org.twz.fit.genetic;

import org.twz.dag.Chromosome;
import org.twz.dataframe.Pair;
import org.twz.fit.ValueDomain;

import java.util.List;

public abstract class AbsCrossover {
    protected final List<ValueDomain> Nodes;

    protected AbsCrossover(List<ValueDomain> nodes) {
        Nodes = nodes;
    }

    public abstract Pair<Chromosome, Chromosome> crossover(Chromosome g1, Chromosome g2);
}
