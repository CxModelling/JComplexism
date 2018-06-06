package org.twz.prob;


import org.apache.commons.math3.distribution.TDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class T extends AdaptorRealCommonsMath {
    public T(String name, Double df) {
        super(name, new TDistribution(df));
    }

    public T(double df) {
        this(null, df);
        Name = String.format("t(%1$s)", df);
    }
}
