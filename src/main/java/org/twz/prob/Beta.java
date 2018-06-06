package org.twz.prob;

import org.apache.commons.math3.distribution.BetaDistribution;


/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Beta extends AdaptorRealCommonsMath {
    public Beta(String name, Double shape1, Double shape2) {
        super(name, new BetaDistribution(shape1, shape2));
    }

    public Beta(double shape1, double shape2) {
        this(null, shape1, shape2);
        Name = String.format("beta(%1$s,%2$s)", shape1, shape2);
    }
}
