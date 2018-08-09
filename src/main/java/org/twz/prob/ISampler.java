package org.twz.prob;

/**
 * Created by TimeWz on 09/08/2018.
 */
public interface ISampler {
    String getName();
    double sample();
    double[] sample(int n);
}
