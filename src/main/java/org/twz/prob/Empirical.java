package org.twz.prob;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;


public class Empirical implements IDistribution {

    private String Name;
    private UnivariateFunction ECDF, InvECDF;
    private double[] Probabilities, Xs;

    public Empirical(String name, double[] ps, double[] xs) {
        assert ps[0] == 0.0;
        assert ps[ps.length - 1] == 1.0;

        Name = name;
        Probabilities = ps;
        Xs = xs;

        ECDF = (new LinearInterpolator()).interpolate(xs, ps);
        InvECDF = (new LinearInterpolator()).interpolate(ps, xs);
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getDataType() {
        return "Double";
    }

    @Override
    public double logProb(double rv) {
        double eps = 1e-6;
        double dx = ECDF.value(rv + eps) - ECDF.value(rv - eps);

        return Math.log(dx/2/eps);
    }

    @Override
    public double sample() {
        return InvECDF.value(Math.random());
    }

    @Override
    public double[] sample(int n) {
        double[] vs = new double[n];
        for (int i = 0; i < n; i++) {
            vs[i] = sample();
        }
        return vs;
    }
}
