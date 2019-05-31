package org.twz.regression.hazard;

public class ZeroInflatedHazardDist extends HazardDist {
    private double PrZero;

    public ZeroInflatedHazardDist(double pr, IHazard hazard, double rr) {
        super(hazard, rr);
        PrZero = pr;
    }

    @Override
    public double logProb(double rv) {
        if (rv <= 0) {
            return Math.log(PrZero);
        } else {
            return Math.log(1-PrZero) + super.logProb(rv);
        }
    }

    @Override
    public double sample() {
        if (Math.random() < PrZero) {
            return 0;
        } else {
            return super.sample();
        }
    }
}
