package fitter.mcmc;

import pcore.*;

/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public interface IStepper {
    Gene step(BayesianModel bm, Gene gene);
    double getStepSize();
    void adaptationOn();
    void adaptationOff();
    boolean isAdaptive();
}
