package pcore.distribution;

import org.apache.commons.math3.distribution.GammaDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Gamma extends AdaptorRealCommonsMath {
    public Gamma(String name, Double shape, Double rate) {
        super(name, new GammaDistribution(shape, 1/rate));
    }

    public Gamma(double shape, double rate) {
        this(null, shape, rate);
        Name = String.format("gamma(%1$s,%2$s)", shape, rate);
    }
}
