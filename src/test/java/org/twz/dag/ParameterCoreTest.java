package org.twz.dag;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.twz.dag.util.NodeGroup;


/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class ParameterCoreTest {

    private BayesNet BN;
    private NodeGroup NG;

    @Before
    public void setUp() throws Exception {
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
    public void toSC_no_ng() throws Exception {
        SimulationCore sc = BN.toSimulationCore();
        ParameterCore pc = sc.generate("X1");

        System.out.println(pc);
        assertArrayEquals(pc.listSamplers().toArray(), new String[]{"y"});

        System.out.println(sc.generate("x1"));
    }


    @Test
    public void toSC_ng() throws Exception {
        NG = new NodeGroup("country", new String[]{"b0", "b1", "x1"});
        NG.appendChildren(new NodeGroup("agent", new String[]{"x2", "mu", "b2"}));
        NG.allocateNodes(BN);
        NG.printBlueprint();
        SimulationCore sc = BN.toSimulationCore(NG, true);
        ParameterCore pc = sc.generate("Taiwan");
        ParameterCore pA = pc.breed("AgA", "agent"), pB = pc.breed("AgB", "agent");

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
