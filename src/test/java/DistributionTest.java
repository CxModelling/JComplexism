import junit.framework.TestCase;
import org.twz.prob.DistributionManager;
import org.twz.prob.IDistribution;

import java.util.Arrays;

/**
 * Created by TimeWz on 2017/11/5.
 */
public class DistributionTest extends TestCase {
    public void testParseDistribution() {
        IDistribution D = DistributionManager.parseDistribution("gamma(0.1, 0.05)");
        System.out.println(Arrays.toString(D.sample(5)));

        System.out.println(D.getLower());
        System.out.println(D.getUpper());
        System.out.println(D.getMean());
    }

}