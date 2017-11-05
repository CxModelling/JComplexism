package pcore.distribution;


import org.apache.commons.math3.distribution.WeibullDistribution;

/**
 * Created by TimeWz on 2017/11/5.
 */
public class Weibull extends AdaptorRealCommonsMath {
    public Weibull(String name, Double alpha, Double beta) {
        super(name, new WeibullDistribution(alpha, beta));
    }

    public Weibull(double alpha, double beta) {
        this(null, alpha, beta);
        Name = String.format("Weibull(%1$s, %2$s)", alpha, beta);
    }
}
