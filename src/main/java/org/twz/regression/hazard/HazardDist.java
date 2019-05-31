package org.twz.regression.hazard;

import org.twz.prob.IDistribution;

public class HazardDist implements IDistribution {
    private IHazard Hazard;
    private double RiskRatio;

    public HazardDist(IHazard hazard, double rr) {
        Hazard = hazard;
        RiskRatio = rr;
    }

    @Override
    public String getName() {
        return Hazard.getClass().getSimpleName();
    }

    @Override
    public String getDataType() {
        return "Double";
    }

    @Override
    public double logProb(double rv) {
        double s1 = -Math.expm1(-Hazard.cumulativeHazard(rv));
        double s2 = -Math.expm1(-Hazard.cumulativeHazard(rv-1e-5));
        return Math.log((s1-s2)/1e-5);
    }

    @Override
    public double sample() {
        double risk = -Math.log(Math.random())/RiskRatio;
        return Hazard.inverseCumulativeHazard(risk);
    }

    @Override
    public double[] sample(int n) {
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            ys[i] = sample();
        }
        return ys;
    }
}
