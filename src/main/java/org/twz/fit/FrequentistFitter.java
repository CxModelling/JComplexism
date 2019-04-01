package org.twz.fit;

import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;

import java.util.List;

public abstract class FrequentistFitter extends AbsFitter {
    public FrequentistFitter() {
        super();
        Options.put("Type", "MLE");
    }

    @Override
    public List<Chromosome> update(BayesianModel bm) {
        warning("There is no update method");
        return bm.getResults();
    }
}
