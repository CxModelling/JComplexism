package org.twz.prob;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;

public class ZeroInflatedWeibull extends AbsZeroInflated {

    public ZeroInflatedWeibull(String name, Double prob, Double alpha, Double beta) {
        super(name, prob, new WeibullDistribution(alpha, beta));
    }

    public ZeroInflatedWeibull(Double prob, Double alpha, Double beta) {
        this(null, prob, alpha, beta);
        Name = String.format("ZIWeibull(%1$s, %2$s, %3$s)", prob, alpha, beta);
    }

}
