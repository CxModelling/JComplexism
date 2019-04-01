package org.twz.fit;

import org.json.JSONObject;
import org.twz.dag.Chromosome;
import org.twz.fit.mcmc.BinaryStepper;
import org.twz.fit.mcmc.DoubleStepper;
import org.twz.fit.mcmc.IStepper;
import org.twz.fit.mcmc.IntegerStepper;
import org.twz.dag.BayesianModel;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public class MCMC extends BayesianFitter {
    private List<IStepper> Steppers;

    public MCMC(int n_post, List<ValueDomain> variables) {
        this(n_post, n_post, variables.size(), variables);
    }

    public MCMC(int n_post, int burn_in, int thin, List<ValueDomain> variables) {
        super();

        Options.put("N_post", n_post);
        Options.put("N_burn_in", burn_in);
        Options.put("N_thin", thin);
        Options.put("N_update", n_post);
        Steppers = new ArrayList<>();

        IStepper stp;

        for (ValueDomain vd: variables) {
            switch (vd.Type) {
                case "Double":
                    stp = new DoubleStepper(vd.Name, vd.Lower, vd.Upper);
                    break;
                case "Integer":
                    stp = new IntegerStepper(vd.Name, vd.Lower, vd.Upper);
                    break;
                case "Binary":
                    stp = new BinaryStepper(vd.Name, vd.Lower, vd.Upper);
                    break;
                default:
                    continue;
            }
            Steppers.add(stp);
        }
    }

    public void adaptationOn() {
        Steppers.forEach(IStepper::adaptationOn);
    }

    public void adaptationOff() {
        Steppers.forEach(IStepper::adaptationOn);
    }

    @Override
    public List<Chromosome> fit(BayesianModel bm) {
        Chromosome last = bm.samplePrior();
        if (!last.isPriorEvaluated()) bm.evaluateLogPrior(last);
        if (!last.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(last);

        int burn = getOptionInteger("N_burn_in");
        int niter = getOptionInteger("N_post");

        last = burnIn(bm, last, burn);
        return collect(bm, last, niter, "Posterior");
    }

    @Override
    public List<Chromosome> update(BayesianModel bm) {
        info("Reading posterior");
        Chromosome last = bm.getResults().get(bm.getResults().size() - 1);
        int niter = getOptionInteger("N_update");
        return collect(bm, last, niter, "Update");
    }

    private Chromosome burnIn(BayesianModel bm, Chromosome init, int burn) {
        info("Burning in");
        Chromosome last = init;
        while (burn > 0) {
            for (IStepper stp: Steppers) {
                last = stp.step(bm, last);
                burn --;
            }
        }
        return last;
    }

    private List<Chromosome> collect(BayesianModel bm, Chromosome init, int n, String tag) {
        info("Collecting posterior");
        List<Chromosome> post = new ArrayList<>();
        Chromosome last = init;
        int thin = getOptionInteger("N_thin");

        int i = 0;
        while (true) {
            for (IStepper stp: Steppers) {
                last = stp.step(bm, last);

                if (i % thin == 0) {
                    post.add(last);
                    bm.keepMemento(last, tag);
                    if (post.size() >= n) {
                        info("Finished");
                        return post;
                    }
                }
                i ++;
            }
        }
    }

    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        return null; // todo
    }
}
