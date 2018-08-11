package org.twz.prob;

import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;

public class DistributionTest {
    @Test
    public void testParseDistribution() {
        IDistribution D = DistributionManager.parseDistribution("gamma(0.1, 0.05)");
        System.out.println(Arrays.toString(D.sample(5)));

        System.out.println(D.getLower());
        System.out.println(D.getUpper());
        System.out.println(D.getMean());
    }

}