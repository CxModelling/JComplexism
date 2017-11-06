package fitter.mcmc;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public class DoubleStepper extends AbsStepper {
    private static NormalDistribution Normal = new NormalDistribution();

    public DoubleStepper(String name, double maxAdaptation, double initialAdaptation, double targetAcceptance, double lo, double up) {
        super(name, maxAdaptation, initialAdaptation, targetAcceptance, lo, up);
    }

    public DoubleStepper(String name, double lo, double up) {
        super(name, lo, up);
    }

    @Override
    protected double proposal(double v, double scale) {
        return v + Normal.sample()*scale;
    }
}
