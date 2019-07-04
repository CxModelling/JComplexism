package org.twz.fit;

import org.json.JSONException;
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
        Options.put("N_max_drop", 100);
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
        int burn = getOptionInteger("N_burn_in");
        int niter = getOptionInteger("N_post");


        Chromosome last;

        try {
            last = bm.getResults().get(0);
        } catch (AssertionError | IndexOutOfBoundsException e) {
            last = initialise(bm);
        }

        last = burnIn(bm, last, burn);
        return collect(bm, last, niter, "Posterior");
    }


    private Chromosome initialise(BayesianModel bm) {
        info("Initialising");
        int niter = getOptionInteger("N_max_drop");
        int k = 1;
        while(true) {
            Chromosome last = bm.samplePrior();
            if (!last.isPriorEvaluated()) bm.evaluateLogPrior(last);
            if (!last.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(last);

            if (last.isLikelihoodEvaluated() & Double.isFinite(last.getLogLikelihood())) {
                return last;
            }

            if (k > niter) {
                error("likelihoods are always infinite");
            }
            k ++;
        }



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
    public OutputSummary getSummary(BayesianModel bm) {
        return getSummary(bm, true);
    }
}
