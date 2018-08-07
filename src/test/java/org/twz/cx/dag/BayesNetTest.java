package org.twz.cx.dag;

import org.junit.Before;
import org.junit.Test;
import org.twz.dag.BayesNet;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNetTest {

    private BayesNet BN;
    private Map<String, Double> Exo;


    @Before
    public void setUp() throws Exception {
        BN = new BayesNet("test");
        BN.appendLoci("age ~ unif(0, 100)");
        BN.appendLoci("sex");
        BN.appendLoci("beta1 = 0.1");
        BN.appendLoci("beta2 = 10");
        BN.appendLoci("mu = age*beta1 + sex*beta2");
        BN.appendLoci("y ~ norm(mu, sd)");

        Exo = new HashMap<>();
        Exo.put("sd", 0.5);
        Exo.put("sex", 1.0);
    }

    @Test
    public void getOrder() throws Exception {
        assertArrayEquals(BN.getOrder().toArray(), new String[]{"sd", "beta1", "beta2", "sex", "age", "mu", "y"});
    }


    @Test
    public void sampleGene() throws Exception {
        System.out.println(BN.sample(Exo));
    }
}
