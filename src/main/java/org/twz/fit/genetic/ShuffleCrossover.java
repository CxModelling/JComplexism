package org.twz.fit.genetic;

import org.twz.dag.Chromosome;
import org.twz.dataframe.Pair;
import org.twz.fit.ValueDomain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShuffleCrossover extends AbsCrossover {
    public ShuffleCrossover(List<ValueDomain> nodes) {
        super(nodes);
    }

    @Override
    public Pair<Chromosome, Chromosome> crossover(Chromosome g1, Chromosome g2) {
        Chromosome p1 = g1.clone(), p2 = g2.clone();
        Map<String, Double> l1 = new HashMap<>(), l2 = new HashMap<>();
        for (ValueDomain vd: Nodes) {
            if (Math.random() < 0.5) {
                l1.put(vd.Name, p2.getDouble(vd.Name));
                l2.put(vd.Name, p1.getDouble(vd.Name));
            } else {
                l1.put(vd.Name, p1.getDouble(vd.Name));
                l2.put(vd.Name, p2.getDouble(vd.Name));
            }
        }
        p1.impulse(l1);
        p2.impulse(l2);
        return new Pair<>(p1, p2);
    }
}
