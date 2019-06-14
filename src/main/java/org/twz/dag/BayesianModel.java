package org.twz.dag;

import org.twz.exception.IncompleteConditionException;
import org.twz.fit.AbsFitter;
import org.twz.fit.OutputSummary;
import org.twz.fit.ValueDomain;
import org.twz.prob.IWalkable;
import org.twz.dag.loci.DistributionLoci;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public abstract class BayesianModel {

    protected final BayesNet BN;
    private AbsFitter Fitter;
    private List<Chromosome> Results, Prior;

    public BayesianModel(BayesNet bn) {
        BN = bn;
    }

    public List<ValueDomain> getMovableNodes() {
        Chromosome p = samplePrior();

        List<ValueDomain> res = new ArrayList<>();
        IWalkable d;
        for (String s : BN.getRVRoots()) {
            try {
                d = (IWalkable) ((DistributionLoci) BN.getLoci(s)).findDistribution(p);
                res.add(new ValueDomain(s, d.getDataType(), d.getLower(), d.getUpper()));
            } catch (ClassCastException | IncompleteConditionException ignored) {}
        }
        return res;
    }

    public abstract Chromosome samplePrior();

    public void evaluateLogPrior(Chromosome chromosome) {
        BN.evaluate(chromosome);
    }

    public abstract void evaluateLogLikelihood(Chromosome chromosome);

    public abstract boolean hasExactLikelihood();

    public abstract void keepMemento(Chromosome chromosome, String type);

    public abstract void clearMementos(String type);

    public abstract void clearMementos();

    public final void generatePrior(int n) {
        Prior = new ArrayList<>();
        clearMementos("Prior");
        int max = 99 * n, drop = 0;
        while (Prior.size() < n) {
            try {
                Chromosome p = samplePrior();
                evaluateLogPrior(p);
                evaluateLogLikelihood(p);
                keepMemento(p, "Prior");
                Prior.add(p);
            } catch (Exception e) {
                drop ++;
            }
            if (drop >= max) {
                break;
            }
        }
        if (Prior.size() < n) {
            System.out.println("Drop rate > 99%");
        }
    }

    public final List<Chromosome> getPriorSample() throws AssertionError {
        assert Prior != null;
        if (Prior.isEmpty()) {
            throw new AssertionError("Prior render have not been generated");
        }
        return Prior;
    }

    public final List<Chromosome> getResults() throws AssertionError {
        assert Results != null;
        if (Results.isEmpty()) {
            throw new AssertionError("Model fitting have not been preceded");
        }
        return Results;
    }

    public OutputSummary getSummary() throws AssertionError {
        assert Results != null;
        if (Results.isEmpty()) {
            throw new AssertionError("Model fitting have not been preceded");
        }
        assert Fitter != null;
        return Fitter.getSummary(this);
    }

    public final void fit(AbsFitter fitter) {
        Fitter = fitter;
        Results = Fitter.fit(this);
    }

    public final void update(Map<String, Object> opts) {
        assert Fitter != null;
        assert Results != null;
        Results = Fitter.update(this, opts);
    }



}
