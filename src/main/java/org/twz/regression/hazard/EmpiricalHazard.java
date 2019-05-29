package org.twz.regression.hazard;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;

public class EmpiricalHazard implements IHazard {
    private UnivariateFunction FnCumHaz, FnInvCumHaz;
    private double MaxTime, MaxHaz;

    public EmpiricalHazard(double[] times, double[] cumHaz) {
        MaxTime = times[times.length - 1];
        MaxHaz = cumHaz[cumHaz.length - 1];
        FnCumHaz = (new LinearInterpolator()).interpolate(times, cumHaz);
        FnInvCumHaz = (new LinearInterpolator()).interpolate(cumHaz, times);
    }

    @Override
    public double cumulativeHazard(double t) {
        return FnCumHaz.value(t);
    }

    @Override
    public double inverseCumulativeHazard(double h) {
        if (h > MaxHaz) return MaxTime;
        return FnInvCumHaz.value(h);
    }
}
