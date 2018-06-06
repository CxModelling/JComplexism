package org.twz.prob;

import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Poisson extends AdaptorIntegerCommonsMath {
    public Poisson(String name, Double lambda) {
        super(name, new PoissonDistribution(lambda));
    }

    public Poisson(double lambda) {
        this(null, lambda);
        Name = String.format("pois(%1$s)", lambda);
    }
}
