package org.twz.dag;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.twz.dag.util.NodeSet;

import java.util.Arrays;


/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class ParameterCoreTest {

    private BayesNet BN;
    private NodeSet NG;

    @Before
    public void setUp() {
        BN = new BayesNet("test");
        BN.appendLoci("b0 = 10");
        BN.appendLoci("b1 ~ norm(0, 1)");
        BN.appendLoci("b2 ~ unif(7, 8)");
        BN.appendLoci("x1 ~ norm(10, 2)");
        BN.appendLoci("x2 ~ binom(3, 0.5)");
        BN.appendLoci("mu = b0 + b1*x1 + b2*x2");
        BN.appendLoci("y ~ norm(mu, 1)");
    }

    @Test
    public void toSC_no_ng() {
        ParameterModel pm = BN.toParameterModel();
        Parameters pc = pm.generate("X1");

        System.out.println(pc);

        assertArrayEquals(pc.listSamplers().toArray(), new String[]{"y"});

        System.out.println(pm.generate("x1"));
    }


    @Test
    public void toSC_ng() {
        NG = new NodeSet("country", new String[]{"b0", "b1", "x1"});
        NG.appendChild(new NodeSet("agent", new String[]{"x2", "mu", "b2"}, new String[]{"y"}));

        ParameterModel sc = BN.toParameterModel(NG);
        Parameters pc = sc.generate("Taiwan");
        Parameters pA = pc.breed("AgA", "agent"), pB = pc.breed("AgB", "agent");

        System.out.println("Likelihood:" + pc.getDeepLogPrior());
        System.out.println(pA);
        System.out.println(pB);

        System.out.println();
        pA.detachFromParent(true);
        pB.detachFromParent(true);
        System.out.println("Likelihood:" + pc.getDeepLogPrior());
        System.out.println(pA);
        System.out.println(pB);

        System.out.println(pA.getSampler("y"));

        System.out.println();
        pA.freeze();
        pB.freeze();
        System.out.println(pA);
        System.out.println(pB);
    }
}
