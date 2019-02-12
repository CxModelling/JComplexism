package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.fit.genetic.AbsCrossover;
import org.twz.fit.genetic.AbsMutator;
import org.twz.fit.genetic.ShuffleCrossover;

import java.util.List;

public class GeneticAlgorithm extends FrequentistFitter {
    private List<AbsMutator> Mutators;
    private AbsCrossover Crossover;
    private List<ValueDomain> Nodes;

    public GeneticAlgorithm(List<ValueDomain> nodes) {
        Crossover = new ShuffleCrossover(nodes);
    }

    @Override
    public List<Gene> fit(BayesianModel bm) {
        return null;
    }

    @Override
    public List<Gene> update(BayesianModel bm) {
        return null;
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }
}
