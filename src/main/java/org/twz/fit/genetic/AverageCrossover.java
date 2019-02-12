package org.twz.fit.genetic;

import org.twz.dag.Gene;
import org.twz.dataframe.Pair;
import org.twz.fit.ValueDomain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageCrossover extends AbsCrossover {
    public AverageCrossover(List<ValueDomain> nodes) {
        super(nodes);
    }

    @Override
    public Pair<Gene, Gene> crossover(Gene g1, Gene g2) {
        Gene p1 = g1.clone(), p2 = g2.clone();
        Map<String, Double> locus = new HashMap<>();

        for (ValueDomain vd: Nodes) {
            double v1 = p1.get(vd.Name), v2 = p2.get(vd.Name);
            locus.put(vd.Name, (v1+v2)/2);
        }
        p1.impulse(locus);
        p2.impulse(locus);
        return new Pair<>(p1, p2);
    }
}
