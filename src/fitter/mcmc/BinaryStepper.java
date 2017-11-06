package fitter.mcmc;

/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public class BinaryStepper extends AbsStepper {
    public BinaryStepper(String name, double maxAdaptation, double initialAdaptation, double targetAcceptance, double lo, double up) {
        super(name, maxAdaptation, initialAdaptation, targetAcceptance, lo, up);
    }

    public BinaryStepper(String name, double lo, double up) {
        super(name, lo, up);
    }

    @Override
    protected double proposal(double v, double scale) {
        return Math.round((Math.random()));
    }
}
