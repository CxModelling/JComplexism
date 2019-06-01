package org.twz.prob;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;

public abstract class AbsZeroInflated extends AdaptorRealCommonsMath {

    private double Probability;
    private BinomialDistribution Zero;

    public AbsZeroInflated(String name, double pr, AbstractRealDistribution d) {
        super(name, d);
        Probability = pr;
        Zero = new BinomialDistribution(1, Probability);
    }

    @Override
    public double sample() {
        if (Zero.sample() ==1) {
            return 0;
        } else {
            return super.sample();
        }
    }

    @Override
    public double[] sample(int n) {
        int[] zs = Zero.sample(n);
        double[] ts = super.sample(n);

        for (int i = 0; i < n; i++) {
            if(zs[i] == 1) ts[i] = 0;
        }
        return ts;
    }

    @Override
    public double logProb(double rv) {
        if (rv == 0) return Math.log(Probability);
        return Math.log(1-Probability)+super.logProb(rv);
    }

    @Override
    public double getLower() {
        return 0;
    }

    @Override
    public double getMean() {
        return super.getMean()*(1-Probability);
    }

    @Override
    public double getStd() {
        return super.getStd()*Math.pow(1-Probability, 2);
    }
}
