package org.twz.prob;

import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * Created by TimeWz on 2017/11/5.
 */
public class Exponential extends AdaptorRealCommonsMath {
    public Exponential(String name, Double rate) {
        super(name, new ExponentialDistribution(1/rate));
    }

    public Exponential(double rate) {
        this(null, rate);
        Name = String.format("exp(%5g)", rate);
    }
}
