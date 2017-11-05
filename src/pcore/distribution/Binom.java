package pcore.distribution;

import org.apache.commons.math3.distribution.BinomialDistribution;


/**
 *
 * Created by TimeWz on 2017/11/5.
 */
public class Binom extends AdaptorIntegerCommonsMath {
    public Binom(String name, Integer size, Double prob) {
        super(name, new BinomialDistribution(size, prob));
    }

    public Binom(int size, double prob) {
        this(null, size, prob);
        Name = String.format("binom(%1$s,%2$s)", size, prob);
    }
}
