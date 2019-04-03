package org.twz.prob;

/**
 * Created by TimeWz on 09/08/2018.
 */
public interface IDistribution {
    String getName();
    String getDataType();
    double logProb(double rv);
    double sample();
    double[] sample(int n);
}
