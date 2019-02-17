package org.twz.fit.genetic;

import org.twz.dag.Gene;

import java.util.List;

public abstract class AbsSelection {
    public abstract List<Gene> select(List<Gene> population, String target);
}
