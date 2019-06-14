package org.twz.fit.mcmc;

import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;

/**
 *  http://probability.ca/jeff/ftpdir/adaptex.pdf
 *  adaptive Metropolis within Gibbs (AMWG) algorithm presented by Roberts and Rosenthal (2009)
 *
 * Created by TimeWz on 25/04/2017.
 */
public abstract class AbsStepper implements IStepper {

    private double MaxAdaptation, InitialAdaptation, TargetAcceptance;
    private int AcceptanceCount, BatchCount, BatchSize, IterationsSinceAdaption;
    private boolean IsAdapting;
    private double LogStepSize, Lower, Upper;
    private String Name;

    public AbsStepper(String name, double maxAdaptation, double initialAdaptation, double targetAcceptance,
                      double lo, double up) {
        Name = name;
        MaxAdaptation = maxAdaptation;
        InitialAdaptation = initialAdaptation;
        TargetAcceptance = targetAcceptance;
        LogStepSize = 0.0;
        AcceptanceCount = 0;
        BatchCount = 0;
        BatchSize = 50;
        IterationsSinceAdaption = 0;
        IsAdapting = true;
        Upper = up;
        Lower = lo;
    }

    public AbsStepper(String name, double lo, double up) {
        this(name, 0.33, 1.0, 0.44, lo, up);
    }

    @Override
    public Chromosome step(BayesianModel bm, Chromosome chromosome) {
        double value = chromosome.getDouble(Name);
        double proposed = proposal(value, getStepSize());
        //if (!chromosome.isEvaluated()) chromosome.setLogLikelihood(bm.evaluateLikelihood(chromosome));
        Chromosome newChromosome = chromosome.clone();

        if (proposed < Lower | proposed > Upper) {
            return newChromosome;
        } else {
            newChromosome.impulse(Name, proposed);

            bm.evaluateLogPrior(newChromosome);
            bm.evaluateLogLikelihood(newChromosome);
            double p_acc = Math.exp(newChromosome.getLogPosterior() - chromosome.getLogPosterior());

            if (p_acc > Math.random()) {
                if (isAdaptive()) AcceptanceCount ++;
            } else {
                newChromosome.impulse(Name, value);
                newChromosome.setLogPriorProb(chromosome.getLogPriorProb());
                newChromosome.setLogLikelihood(chromosome.getLogLikelihood());
            }
        }

        if (isAdaptive()) {
            IterationsSinceAdaption ++;
            if (IterationsSinceAdaption >= BatchSize) {
                BatchCount ++;
                double adj = Math.min(MaxAdaptation, InitialAdaptation/Math.sqrt(BatchCount));
                if (AcceptanceCount > TargetAcceptance * BatchSize) {
                    LogStepSize += adj;
                } else {
                    LogStepSize -= adj;
                }
                AcceptanceCount = 0;
                IterationsSinceAdaption = 0;
            }
        }
        return newChromosome;
    }

    protected abstract double proposal(double v, double scale);

    @Override
    public double getStepSize() {
        return Math.exp(LogStepSize);
    }

    @Override
    public void adaptationOn() {
        IsAdapting = true;
    }

    @Override
    public void adaptationOff() {
        IsAdapting = false;
    }

    @Override
    public boolean isAdaptive() {
        return IsAdapting;
    }

    @Override
    public String toString() {
        return "Stepper " + Name + "{"+
                "AcceptanceCount=" + AcceptanceCount +
                ", IterationsSinceAdaption=" + IterationsSinceAdaption +
                ", LogStepSize=" + LogStepSize +
                '}';
    }


}
