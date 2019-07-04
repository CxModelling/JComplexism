package org.twz.prob;

import java.util.Arrays;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.junit.Test;
import org.twz.exception.IncompleteConditionException;
import org.twz.util.Statistics;

public class DistributionTest {
    @Test
    public void testParseDistribution() {
        IWalkable D = DistributionManager.parseDistribution("gamma(0.1, 0.05)");
        System.out.println(Arrays.toString(D.sample(5)));

        System.out.println(D.getLower());
        System.out.println(D.getUpper());
        System.out.println(D.getMean());
    }


    @Test
    public void testSample() {
        IWalkable D = DistributionManager.parseDistribution("unif(0.003, 0.05)");
        System.out.println(Statistics.min(D.sample(100000)));

        System.out.println(D.getLower());
        System.out.println(D.getUpper());
        System.out.println(D.getMean());
    }

    @Test
    public void testSampleBinom() {
        IWalkable D = DistributionManager.parseDistribution("binom(1, 0.5)");
        System.out.println(Arrays.toString(D.sample(5)));
        System.out.println(Statistics.mean(D.sample(100000)));

        System.out.println(D.getLower());
        System.out.println(D.getUpper());
        System.out.println(D.getMean());

    }


}