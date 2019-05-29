package org.twz.regression.hazard;

public class ExponentialHazard implements IHazard {
    private double Rate;

    public ExponentialHazard(double rate) {
        Rate = rate;
    }

    @Override
    public double cumulativeHazard(double t) {
        return Rate*t;
    }

    @Override
    public double inverseCumulativeHazard(double h) {
        return h/Rate;
    }
}
