package fitter.mcmc;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public class IntegerStepper extends AbsStepper {
    private static NormalDistribution Normal = new NormalDistribution();

    public IntegerStepper(String name, double maxAdaptation, double initialAdaptation, double targetAcceptance, double lo, double up) {
        super(name, maxAdaptation, initialAdaptation, targetAcceptance, lo, up);
    }

    public IntegerStepper(String name, double lo, double up) {
        super(name, lo, up);
    }

    @Override
    protected double proposal(double v, double scale) {
        return Math.round(v + Normal.sample()*scale);
    }
}
