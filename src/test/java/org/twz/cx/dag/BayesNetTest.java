package org.twz.cx.dag;

import org.junit.Before;
import org.junit.Test;
import org.twz.dag.BayesNet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNetTest {

    private BayesNet BN;

    @Before
    public void setUp() throws Exception {
        BN = new BayesNet("test");
        BN.appendLoci("age ~ unif(0, 100)");
        BN.appendLoci("sex");
        BN.appendLoci("beta1 = 10");
        BN.appendLoci("beta2 = 10");
        BN.appendLoci("mu = age*beta1 + sex*beta2");
        BN.appendLoci("y ~ norm(mu, sd)");
    }

    @Test
    public void getOrder() throws Exception {
        assertArrayEquals(BN.getOrder().toArray(), new String[]{"sd", "beta1", "beta2", "sex", "age", "mu", "y"});
    }


}
