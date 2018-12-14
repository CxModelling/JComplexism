package org.twz.dag;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNetTest {

    private BayesNet BN;
    private Map<String, Double> Exo;


    @Before
    public void setUp() {
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
    public void getOrder() {
        List<String> od = new ArrayList<>();
        od.add("sd");
        od.add("beta1");
        od.add("beta2");
        od.add("sex");
        od.add("age");
        od.add("mu");
        od.add("y");
        assertEquals(BN.getOrder(), od);
    }


    @Test
    public void sampleGene() {
        System.out.println(BN.sample(Exo));
    }
}
