package org.twz.fit;


import org.apache.commons.math3.stat.StatUtils;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;
import org.twz.dataframe.Pair;
import org.twz.fit.genetic.*;
import org.twz.prob.Sample;
import org.twz.util.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ABCSMC extends BayesianFitter {
    private List<AbsMutator> Mutators;
    private double Epsilon;
    private double[] Weights;

    public ABCSMC(int n_post, List<ValueDomain> nodes) {
        Mutators = new ArrayList<>();

        Options.put("N_test", 100);
        Options.put("P_test", 0.5);
        Options.put("N_population", n_post);
        Options.put("Max_generation", 10);
        Options.put("Max_stay", 5);
        Options.put("P_eps", 0.70);
        Epsilon = Double.NaN;

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
        if (Double.isNaN(Epsilon)) {
            test(bm);
        }

        int max_g = getOptionInteger("Max_generation");
        int n_g = 0, n_stay = 0;

        double eps0;

        info("Genesis");


        Pair<double[], List<Chromosome>> population = genesis(bm);

        eps0 = Statistics.max(getLikelihood(population.getSecond()));

        while(n_g < max_g) {
            n_g ++;
            population = reproduce(bm, population);

            if (Math.abs(eps0 - Epsilon) < 0.05*Math.abs(Epsilon)) {
                n_stay ++;
            } else {

                n_stay = 0;
            }
            eps0 = Epsilon;
            info(String.format("Generation: %d, Eps: %g, ESS: %d", n_g, Epsilon, (int) Statistics.ess(population.getFirst())));

            if(canBeTerminated(n_stay)) {
                break;
            }
        }

        info("Fitting completed");

        population.getSecond().forEach(p->bm.keepMemento(p, "Posterior"));
        Weights = population.getFirst();
        return population.getSecond();
    }

    private void test(BayesianModel bm) {
        info("Testing run");
        int n = getOptionInteger ("N_test");
        double p = getOptionDouble("P_test");

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

        double[] lis = getLikelihood(xs);

        Epsilon = Statistics.quantile(lis, 1-p);
        info("Test render suggest epi=" + Epsilon);
    }

    private Pair<double[], List<Chromosome>> genesis(BayesianModel bm) {
        bm.clearMementos("Genesis");
        int n = getOptionInteger("N_population");
        List<Chromosome> xs = new ArrayList<>();

        Chromosome chromosome;
        while (xs.size() < n) {
            chromosome = bm.samplePrior();
            if (!chromosome.isPriorEvaluated()) bm.evaluateLogPrior(chromosome);
            if (!chromosome.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(chromosome);
            if (chromosome.getLogLikelihood() > Epsilon) {
                xs.add(chromosome);
                bm.keepMemento(chromosome, "Genesis");
            }
        }
        double[] wts = new double[n];
        for (int i = 0; i < n; i++) {
            wts[i] = 1.0/n;
        }
        updateMutators(xs);
        return new Pair<>(wts, xs);
    }

    private Pair<double[], List<Chromosome>> reproduce(BayesianModel bm, Pair<double[], List<Chromosome>> population) {
        int n = population.getSecond().size();
        double n_exp = n / getOptionDouble("P_eps");
        n_exp = Math.max(n_exp, n);

        List<Chromosome> p0 = population.getSecond();
        List<Chromosome> xs = new ArrayList<>();

        Sample s = new Sample(population.getFirst());

        Chromosome chr;
        while (xs.size() < n_exp) {
            chr = p0.get(s.sample());
            chr = mutate(chr);
            if (!chr.isPriorEvaluated()) bm.evaluateLogPrior(chr);
            if (!chr.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(chr);
            xs.add(chr);
        }

        Epsilon = Math.max(Epsilon, Statistics.quantile(getLikelihood(xs), 1-getOptionDouble("P_eps")));
        xs.removeIf(d->d.getLogLikelihood() < Epsilon);
        while (xs.size() > n) {
            xs.remove(0);
        }
        while (xs.size() < n) {
            chr = p0.get(s.sample());
            chr = mutate(chr);
            if (!chr.isPriorEvaluated()) bm.evaluateLogPrior(chr);
            if (!chr.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(chr);
            if (chr.getLogLikelihood() > Epsilon) xs.add(chr);
        }
        double[] wts = calculateWts(population.getFirst(), p0, xs);
        updateMutators(xs);
        return new Pair<>(wts, xs);
    }

    private void updateMutators(List<Chromosome> population) {
        double[] vs;
        for (AbsMutator mutator : Mutators) {
            vs = new double[population.size()];
            for (int i = 0; i < vs.length; i++) {
                vs[i] = population.get(i).getDouble(mutator.Name);
            }
            mutator.setScale(vs);
        }
    }

    private Chromosome mutate(Chromosome chr) {
        chr = chr.clone();
        Map<String, Double> locus = new HashMap<>();
        for (AbsMutator mutator : Mutators) {
            locus.put(mutator.Name, mutator.propose(chr.getDouble(mutator.Name)));
        }
        chr.impulse(locus);
        chr.resetProbability();
        return chr;
    }

    private double[] calculateWts(double[] wts0, List<Chromosome> p0, List<Chromosome> p1) {
        int n = wts0.length;
        double[] wts = new double[n];

        Chromosome chr1;
        for (int i = 0; i < n; i++) {
            chr1 = p1.get(i);
            double base = 0;

            for (int j = 0; j < n; j++) {
                base += distance(p0.get(j), chr1)*wts0[j];
            }

            wts[i] = Math.exp(chr1.getLogPriorProb())/base;
        }
        wts = Statistics.mul(wts, 1/Statistics.sum(wts));
        return wts;
    }

    private double[] getLikelihood(List<Chromosome> population) {
        return population.stream().mapToDouble(Chromosome::getLogLikelihood).toArray();
    }

    private double distance(Chromosome chr0, Chromosome chr1) {
        double x=0;
        for (AbsMutator mutator : Mutators) {
            x += mutator.calculateLogKernel(chr0.getDouble(mutator.Name), chr1.getDouble(mutator.Name));
        }
        return Math.exp(x);
    }

    private boolean canBeTerminated(int n_stay) {
        return n_stay >= getOptionInteger("Max_stay");
    }

    @Override
    public List<Chromosome> update(BayesianModel bm) {
        return null;
    }

    @Override
    public OutputSummary getSummary(BayesianModel bm) {
        OutputSummary summary = getSummary(bm, false);
        summary.setESS((int) Statistics.ess(Weights));
        return summary;
    }


}
