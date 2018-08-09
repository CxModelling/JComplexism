package org.twz.prob;

/**
 *
 * Created by TimeWz on 2017/4/17.
 */
public interface IDistribution extends ISampler {
    double logpdf(double rv);
    String getDataType();
    double getUpper();
    double getLower();
    double getMean();
    double getStd();
}
