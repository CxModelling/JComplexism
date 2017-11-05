package test;

import junit.framework.TestCase;
import pcore.distribution.DistributionManager;
import pcore.distribution.IDistribution;

import java.util.Arrays;

/**
 * Created by TimeWz on 2017/11/5.
 */
public class DistributionTest extends TestCase {
    public void testParseDistribution() throws Exception {
        IDistribution D = DistributionManager.parseDistribution("gamma(0.1, 0.05)");
        System.out.println(Arrays.toString(D.sample(5)));

        System.out.println(D.getLower());
        System.out.println(D.getUpper());
        System.out.println(D.getMean());
    }

}