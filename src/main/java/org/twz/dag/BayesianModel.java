package org.twz.dag;

import org.twz.fit.AbsFitter;
import org.twz.fit.ValueDomain;
import org.twz.prob.IDistribution;
import org.twz.dag.loci.DistributionLoci;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public abstract class BayesianModel {

    public final BayesNet BN;
    private AbsFitter Fitter;
    private List<Gene> Results, Prior;

    public BayesianModel(BayesNet bn) {
        BN = bn;
    }

    public List<ValueDomain> getMovableNodes() {
        Gene p = samplePrior();

        List<ValueDomain> res = new ArrayList<>();
        IDistribution d;
        for (String s : BN.getRVRoots()) {
            d = ((DistributionLoci) BN.getLoci(s)).findDistribution(p);
            res.add(new ValueDomain(s, d.getDataType(), d.getLower(), d.getUpper()));
        }
        return res;
    }

    public abstract Gene samplePrior();

    public void evaluateLogPrior(Gene gene) {
        BN.evaluate(gene);
    }

    public abstract void evaluateLogLikelihood(Gene gene);

    public abstract boolean hasExactLikelihood();

    public abstract void keepMemento(Gene gene, String type);

    public abstract void clearMementos(String type);

    public abstract void clearMementos();

    public final void generatePrior(int n) {
        Prior = new ArrayList<>();
        clearMementos("Prior");
        int max = 99 * n, drop = 0;
        while (Prior.size() < n) {
            try {
                Gene p = samplePrior();
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

    public final List<Gene> getPriorSample() throws AssertionError {
        assert Prior != null;
        if (Prior.isEmpty()) {
            throw new AssertionError("Prior sample have not been generated");
        }
        return Prior;
    }

    public final List<Gene> getResults() throws AssertionError {
        assert Results != null;
        if (Results.isEmpty()) {
            throw new AssertionError("Model fitting have not been preceded");
        }
        return Results;
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
