package fitter.mcmc;

import pcore.BayesianModel;
import pcore.Gene;

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
    public Gene step(BayesianModel bm, Gene gene) {
        double value = gene.get(Name);
        double proposed = proposal(value, getStepSize());
        //if (!gene.isEvaluated()) gene.setLogLikelihood(bm.evaluateLikelihood(gene));
        Gene newGene = gene.clone();

        if (proposed < Lower | proposed > Upper) {
            return newGene;
        } else {
            newGene.put(Name, proposed);
            newGene.setLogPriorProb(bm.evaluatePrior(newGene));
            newGene.setLogLikelihood(bm.evaluateLikelihood(newGene));
            double p_acc = Math.exp(newGene.getLogPosterior() - gene.getLogPosterior());

            if (p_acc > Math.random()) {
                if (isAdaptive()) AcceptanceCount ++;
            } else {
                newGene.put(Name, value);
                newGene.setLogPriorProb(gene.getLogPriorProb());
                newGene.setLogLikelihood(gene.getLogLikelihood());
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
        return newGene;
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
