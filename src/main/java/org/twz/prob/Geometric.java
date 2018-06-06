package org.twz.prob;

import org.apache.commons.math3.distribution.GeometricDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Geometric extends AdaptorIntegerCommonsMath {
    public Geometric(String name, Double prob) {
        super(name, new GeometricDistribution(prob));
    }

    public Geometric(double prob) {
        this(null, prob);
        Name = String.format("geom(%1$s)", prob);
    }
}
