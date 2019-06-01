package org.twz.prob;


import org.apache.commons.math3.distribution.ExponentialDistribution;

public class ZeroInflatedExponential extends AbsZeroInflated {

    public ZeroInflatedExponential(String name, Double prob, Double rate) {
        super(name, prob, new ExponentialDistribution(rate));
        Name = name;
    }

    public ZeroInflatedExponential(Double prob, Double rate) {
        this(null, prob, rate);
        Name = String.format("ZIExp(%5g, %5g)", prob, rate);
    }
}
