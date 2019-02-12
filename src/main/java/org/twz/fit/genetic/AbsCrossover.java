package org.twz.fit.genetic;

import org.twz.dag.Gene;
import org.twz.dataframe.Pair;
import org.twz.fit.ValueDomain;

import java.util.List;

public abstract class AbsCrossover {
    protected final List<ValueDomain> Nodes;

    protected AbsCrossover(List<ValueDomain> nodes) {
        Nodes = nodes;
    }

    public abstract Pair<Gene, Gene> crossover(Gene g1, Gene g2);
}
