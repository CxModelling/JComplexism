package org.twz.prob;

import org.apache.commons.math3.distribution.LogNormalDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Lognormal extends AdaptorRealCommonsMath {
    public Lognormal(String name, Double meanlog, Double sdlog) {
        super(name, new LogNormalDistribution(meanlog, sdlog));
    }

    public Lognormal(double meanlog, double sdlog) {
        this(null, meanlog, sdlog);
        Name = String.format("lnorm(%1$s,%2$s)", meanlog, sdlog);
    }
}
