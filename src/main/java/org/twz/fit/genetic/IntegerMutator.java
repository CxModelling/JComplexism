package org.twz.fit.genetic;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.twz.fit.ValueDomain;

import static org.twz.util.Statistics.quantile;

public class IntegerMutator extends AbsMutator {
    private static NormalDistribution Normal = new NormalDistribution();

    private double Scale;

    public IntegerMutator(ValueDomain vd) {
        super(vd);
    }

    @Override
    public void setScale(double[] vs) {
        double lo = quantile(vs, 0.75) - quantile(vs, 0.25);
        lo = Math.min(lo, lo/1.34);
        Scale = 0.9 * lo * Math.pow(vs.length, -0.2);
    }

    @Override
    public double propose(double v) {
        while (true) {
            double x = Math.round(v + Normal.sample()*Scale);
            if (x > Lower | x < Upper) return x;
        }
    }

    @Override
    public double calculateLogKernel(double v1, double v2) {
        return Normal.logDensity((v1-v2)/Scale);
    }
}
