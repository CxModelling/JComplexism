package org.twz.fit.genetic;

import org.twz.dag.Chromosome;
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
    public Pair<Chromosome, Chromosome> crossover(Chromosome g1, Chromosome g2) {
        Chromosome p1 = g1.clone(), p2 = g2.clone();
        Map<String, Double> locus = new HashMap<>();

        for (ValueDomain vd: Nodes) {
            double v1 = p1.getDouble(vd.Name), v2 = p2.getDouble(vd.Name);
            locus.put(vd.Name, (v1+v2)/2);
        }
        p1.impulse(locus);
        p2.impulse(locus);
        return new Pair<>(p1, p2);
    }
}
