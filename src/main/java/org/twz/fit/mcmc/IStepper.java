package org.twz.fit.mcmc;

import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;

/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public interface IStepper {
    Chromosome step(BayesianModel bm, Chromosome chromosome);
    double getStepSize();
    void adaptationOn();
    void adaptationOff();
    boolean isAdaptive();
}
