package pcore.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Normal extends AdaptorRealCommonsMath {
    public Normal(String name, Double mean, Double sd) {
        super(name, new NormalDistribution(mean, sd));
    }

    public Normal(double mean, double sd) {
        this(null, mean, sd);
        Name = String.format("norm(%1$s,%2$s)", mean, sd);
    }
}
