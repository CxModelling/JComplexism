package org.twz.regression.hazard;

public class WeibullHazard implements IHazard {
    private double Lambda, K;

    public WeibullHazard(double lambda, double k) {
        Lambda = lambda;
        K = k;
    }

    @Override
    public double cumulativeHazard(double t) {
        return Math.pow(Lambda*t, K);
    }

    @Override
    public double inverseCumulativeHazard(double h) {
        return Math.pow(h, 1/K)/Lambda;
    }
}
