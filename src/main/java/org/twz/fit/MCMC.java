package org.twz.fit;

import org.twz.fit.mcmc.BinaryStepper;
import org.twz.fit.mcmc.DoubleStepper;
import org.twz.fit.mcmc.IStepper;
import org.twz.fit.mcmc.IntegerStepper;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public class MCMC extends BayesianFitter {
    private List<IStepper> Steppers;
    private Gene Last;
    private int Burnin, Thin;

    public MCMC(BayesianModel model) {
        this(model, 1000, 3);
    }

    public MCMC(BayesianModel model, int burn, int thin) {
        super(model);
        Posterior = new ArrayList<>();
        Steppers = new ArrayList<>();
        Burnin = burn;
        Thin = thin;

        IStepper stp;

        for (ValueDomain vd: Model.getMovableNodes()) {

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

    @Override
    public Map<String, Double> getGoodnessOfFit() {
        return null;
    }

    public void fit(int niter) {
        initialise();
        int nSample = 0;
        while (true) {
            for (IStepper stp: Steppers) {
                nSample ++;
                Last = stp.step(Model, Last);

                if (nSample > Burnin & nSample % Thin == 0) Posterior.add(Last);
                if (Posterior.size() >= niter) return;
            }
        }
    }

    public void initialise() {
        Posterior.clear();
        Last = Model.samplePrior();
        Model.evaluateLogLikelihood(Last);
    }

    public void adaptationOn() {
        Steppers.forEach(IStepper::adaptationOn);
    }

    public void adaptationOff() {
        Steppers.forEach(IStepper::adaptationOn);
    }

    public void update(int niter) {
        int nSample = 0;
        niter += Posterior.size();
        while (true) {
            for (IStepper stp: Steppers) {
                nSample ++;
                Last = stp.step(Model, Last);
                Model.evaluateLogLikelihood(Last);
                if (nSample % Thin == 0) Posterior.add(Last);
                if (Posterior.size() >= niter) return;
            }
        }

    }

}
