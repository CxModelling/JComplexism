package org.twz.prob;

import org.twz.exception.IncompleteConditionException;

/**
 * Created by TimeWz on 09/08/2018.
 */
public interface IDistribution {
    String getName();
    String getDataType();
    double logProb(double rv);
    double sample() throws IncompleteConditionException;
    double[] sample(int n) throws IncompleteConditionException;
}
