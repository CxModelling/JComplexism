package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;
import org.twz.dataframe.Pair;
import org.twz.fit.genetic.*;
import org.twz.util.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithm extends FrequentistFitter {
    private AbsSelection Selector;
    private List<AbsMutator> Mutators;
    private AbsCrossover Crossover;

    public GeneticAlgorithm(List<ValueDomain> nodes) {
        Selector = new TournamentSelection();
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
    public List<Chromosome> fit(BayesianModel bm) {
        int max_g = getOptionInteger("Max_generation");
        String type = getOptionString("Target");
        int n_g = 0, n_stay = 0;
        Chromosome elite = null;
        double max_fitness = Double.NEGATIVE_INFINITY, mean_fitness;

        info("Genesis");
        List<Chromosome> population = genesis(bm);

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
            } else {
                n_stay = 0;
            }
            max_fitness = li;
            if (type.equals("MLE")) {
                mean_fitness = Statistics.lse(population.stream()
                        .mapToDouble(Chromosome::getLogLikelihood).toArray());
            } else {
                mean_fitness = Statistics.lse(population.stream()
                        .mapToDouble(Chromosome::getLogPosterior).toArray());
            }
            mean_fitness -= Math.log(population.size());
            info(String.format("Generation: %d, Max: %g, Mean: %g", n_g, max_fitness, mean_fitness));

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
        List<Chromosome> res = new ArrayList<>();
        res.add(elite);
        return res;

    }

    @Override
    public OutputSummary getSummary(BayesianModel bm) {
        return null; // todo
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null;
    }

    private List<Chromosome> genesis(BayesianModel bm) {
        bm.clearMementos("Genesis");
        int n = getOptionInteger("N_population");
        List<Chromosome> xs = new ArrayList<>();

        try {
            for (Chromosome chromosome : (bm.getPriorSample())) {
                xs.add(chromosome);
                if (xs.size() >= n) {
                    break;
                }
            }
        } catch (AssertionError ignored) {

        }

        appendPriorUntil(bm, n, xs);
        return xs;
    }

    private List<Chromosome> selection(List<Chromosome> population) {
        return Selector.select(population, getOptionString("Target"));
    }

    private void mutation(List<Chromosome> population) {
        double p = getOptionDouble("P_mutation");
        double[] vs;
        for (AbsMutator mutator : Mutators) {
            vs = new double[population.size()];
            for (int i = 0; i < vs.length; i++) {
                vs[i] = population.get(i).getDouble(mutator.Name);
            }
            mutator.setScale(vs);
        }

        Map<String, Double> locus;
        for (Chromosome chromosome : population) {
            if (Math.random() > p) continue;

            locus = new HashMap<>();
            for (AbsMutator mutator : Mutators) {
                locus.put(mutator.Name, mutator.propose(chromosome.getDouble(mutator.Name)));
            }
            chromosome.impulse(locus);
            chromosome.resetProbability();
        }
    }

    private void crossover(List<Chromosome> population) {
        double p = getOptionDouble("P_crossover");

        GeneticAlgorithm.crossover(population, p, Crossover);
    }

    static void crossover(List<Chromosome> population, double p, AbsCrossover crossover) {
        int m = population.size()/2;

        Chromosome p1, p2;
        Pair<Chromosome, Chromosome> offspring;
        for (int i = 0; i < m; i++) {
            if (Math.random() > p) continue;
            p1 = population.get(2*i);
            p2 = population.get(2*i+1);
            offspring = crossover.crossover(p1, p2);
            offspring.getFirst().resetProbability();
            offspring.getSecond().resetProbability();
            population.remove(p1);
            population.remove(p2);
            population.add(offspring.getFirst());
            population.add(offspring.getSecond());
        }
    }


    private Chromosome findElitism(List<Chromosome> population) {
        Chromosome elite = null;
        String type = getOptionString("Target");
        double max = Double.NEGATIVE_INFINITY, li;
        for (Chromosome chromosome : population) {
            li = type.equals("MLE")? chromosome.getLogLikelihood(): chromosome.getLogPosterior();
            if (li > max) {
                max = li;
                elite = chromosome;
            }
        }
        return elite;
    }

    private boolean canBeTerminated(int n_stay) {
        return n_stay >= getOptionInteger("Max_stay");
    }

    private void checkEvaluation(BayesianModel bm, Chromosome chromosome) {
        if (!chromosome.isPriorEvaluated()) bm.evaluateLogPrior(chromosome);
        if (!chromosome.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(chromosome);
    }

    private void checkEvaluation(BayesianModel bm, List<Chromosome> chromosomes) {
        for (Chromosome chromosome : chromosomes) {
            checkEvaluation(bm, chromosome);
        }
    }
}
