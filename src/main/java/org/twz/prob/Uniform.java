package org.twz.prob;

import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Uniform extends AdaptorRealCommonsMath {
    public Uniform(String name, Double min, Double max) {
        super(name, new UniformRealDistribution(min, max));
    }

    public Uniform(double min, double max) {
        this(null, min, max);
        Name = String.format("unif(%1$s,%2$s)", min, max);
    }
}
