package org.twz.prob;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Chi2 extends AdaptorRealCommonsMath {
    public Chi2(String name, Double df) {
        super(name, new ChiSquaredDistribution(df));
    }

    public Chi2(double df) {
        this(null, df);
        Name = String.format("chisq(%1$s)", df);
    }
}
