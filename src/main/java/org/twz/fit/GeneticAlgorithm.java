package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.dataframe.Pair;
import org.twz.fit.genetic.*;
import org.twz.misc.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithm extends FrequentistFitter {
    private AbsSelection Selector;
    private List<AbsMutator> Mutators;
    private AbsCrossover Crossover;

    public GeneticAlgorithm(List<ValueDomain> nodes) {
        Selector = new ImportanceSelection();
        Mutators = new ArrayList<>();
        Crossover = new ShuffleCrossover(nodes);

        Options.put("N_population", 500);
        Options.put("P_mutation", 0.1);
        Options.put("P_crossover", 0.1);
        Options.put("Max_stay", 5);
        Options.put("Max_generation", 20);
        Options.put("Target", "MLE");

        AbsMutator mut;
        for (ValueDomain node : nodes) {
            switch (node.Type) {
                case "Double":
                    mut = new DoubleMutator(node);
                    break;
                case "Integer":
                    mut = new IntegerMutator(node);
                    break;
                case "Binary":
                    mut = new BinaryMutator(node);
                    break;
                default:
                    mut = new DoubleMutator(node);
            }
            Mutators.add(mut);
        }
    }

    @Override
    public List<Gene> fit(BayesianModel bm) {
        int max_g = getOptionInteger("Max_generation");
        String type = getOptionString("Target");
        int n_g = 0, n_stay = 0;
        Gene elite = null;
        double max_fitness = Double.NEGATIVE_INFINITY, mean_fitness;

        List<Gene> population = genesis(bm);

        while(n_g < max_g) {
            n_g ++;
            crossover(population);
            mutation(population);
            checkEvaluation(bm, population);
            population = selection(population);

            elite = findElitism(population);
            double li = type.equals("MLE")?elite.getLogLikelihood():elite.getLogPosterior();
            if (li == max_fitness) {
                n_stay ++;
            }
            max_fitness = li;
            if (type.equals("MLE")) {
                mean_fitness = Statistics.lse(population.stream()
                        .mapToDouble(Gene::getLogLikelihood).toArray());
            } else {
                mean_fitness = Statistics.lse(population.stream()
                        .mapToDouble(Gene::getLogPosterior).toArray());
            }
            mean_fitness -= Math.log(population.size());

            info("Generation: " + n_g +
                    ", Max fitness: " + max_fitness +
                    ", Mean fitness: " + mean_fitness);

            if(canBeTerminated(n_stay)) {
                break;
            }
        }

        if (elite == null) {
            error("Fitting failed");
            return null;
        }
        info("Fitting completed");
        bm.keepMemento(elite, type);
        List<Gene> res = new ArrayList<>();
        res.add(elite);
        return res;

    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }

    private List<Gene> genesis(BayesianModel bm) {
        bm.clearMementos("Genesis");
        int n = getOptionInteger("N_population");
        List<Gene> xs = new ArrayList<>();

        try {
            for (Gene gene : (bm.getPriorSample())) {
                xs.add(gene);
                if (xs.size() >= n) {
                    break;
                }
            }
        } catch (AssertionError ignored) {

        }

        appendPriorUntil(bm, n, xs);
        return xs;
    }

    private List<Gene> selection(List<Gene> population) {
        return Selector.select(population, getOptionString("Target"));
    }

    private void mutation(List<Gene> population) {
        double p = getOptionDouble("P_mutation");
        double[] vs;
        for (AbsMutator mutator : Mutators) {
            vs = new double[population.size()];
            for (int i = 0; i < vs.length; i++) {
                vs[i] = population.get(i).get(mutator.Name);
            }
            mutator.setScale(vs);
        }

        Map<String, Double> locus;
        for (Gene gene : population) {
            if (Math.random() > p) continue;

            locus = new HashMap<>();
            for (AbsMutator mutator : Mutators) {
                locus.put(mutator.Name, mutator.propose(gene.get(mutator.Name)));
            }
            gene.impulse(locus);
        }
    }

    private void crossover(List<Gene> population) {
        double p = getOptionDouble("P_crossover");

        int m = population.size()/2;

        Gene p1, p2;
        Pair<Gene, Gene> offspring;
        for (int i = 0; i < m; i++) {
            if (Math.random() > p) continue;
            p1 = population.get(2*i);
            p2 = population.get(2*i+1);
            offspring = Crossover.crossover(p1, p2);
            population.add(2*i, offspring.getFirst());
            population.add(2*i+1, offspring.getSecond());
        }
    }

    private Gene findElitism(List<Gene> population) {
        Gene elite = null;
        String type = getOptionString("Target");
        double max = Double.NEGATIVE_INFINITY, li;
        for (Gene gene : population) {
            li = type.equals("MLE")?gene.getLogLikelihood():gene.getLogPosterior();
            if (li > max) {
                max = li;
                elite = gene;
            }
        }
        return elite;
    }

    private boolean canBeTerminated(int n_stay) {
        return n_stay >= getOptionInteger("Max_stay");
    }

    private void checkEvaluation(BayesianModel bm, Gene gene) {
        if (!gene.isPriorEvaluated()) bm.evaluateLogPrior(gene);
        if (!gene.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(gene);
    }

    private void checkEvaluation(BayesianModel bm, List<Gene> genes) {
        for (Gene gene : genes) {
            checkEvaluation(bm, gene);
        }
    }
}
