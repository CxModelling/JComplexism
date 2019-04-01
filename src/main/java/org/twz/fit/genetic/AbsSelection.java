package org.twz.fit.genetic;

import org.twz.dag.Chromosome;

import java.util.List;

public abstract class AbsSelection {
    public abstract List<Chromosome> select(List<Chromosome> population, String target);
}
