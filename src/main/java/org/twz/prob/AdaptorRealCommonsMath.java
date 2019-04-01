package org.twz.prob;


import org.apache.commons.math3.distribution.AbstractRealDistribution;


/**
 * An adaptor for integer distribution in Apache commons-math
 * Created by TimeWz on 2017/7/15.
 */
public class AdaptorRealCommonsMath implements IWalkable {
    private AbstractRealDistribution D;
    protected String Name;

    public AdaptorRealCommonsMath(String name, AbstractRealDistribution d) {
        Name = name;
        D = d;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public double sample() {
        return D.sample();
    }

    @Override
    public double[] sample(int n) {
        return D.sample(n);
    }

    @Override
    public double logProb(double rv) {
        return D.logDensity(rv);
    }

    @Override
    public String getDataType() {
        return "Double";
    }

    @Override
    public double getUpper() {
        return D.getSupportUpperBound();
    }

    @Override
    public double getLower() {
        return D.getSupportLowerBound();
    }

    @Override
    public double getMean() {
        return D.getNumericalMean();
    }

    @Override
    public double getStd() {
        return Math.sqrt(D.getNumericalVariance());
    }

    public String toString() {
        return Name;
    }
}
