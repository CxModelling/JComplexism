package org.twz.dag;

import org.junit.Before;
import org.junit.Test;
import org.twz.exception.ValidationException;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class ParameterTest {

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
        BN.appendLoci("z = if(y>0, 1, -1)");
    }

    @Test
    public void toSC_no_ng() throws ValidationException {
        ParameterModel pm = BN.toParameterModel();
        Parameters pc = pm.generate("X1");

        System.out.println(pc);

        System.out.println(pm.generate("x1"));
    }


    @Test
    public void toSC_ng() throws ValidationException {
        NG = new NodeSet("country", new String[]{"b0", "b1", "x1"});
        NG.appendChild(new NodeSet("agent", new String[]{"x2", "b2"}, new String[]{"y", "z"}));
        NG.printAll();
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

    @Test
    public void SC_exo() throws ValidationException {
        NG = new NodeSet("country", new String[]{"b0", "b1", "x1"});
        NG.appendChild(new NodeSet("agent", new String[]{"x2", "mu", "b2"}, new String[]{"y"}));

        ParameterModel sc = BN.toParameterModel(NG);
        Map<String, Double> exo = new HashMap<>();
        exo.put("x2", 2.0);
        Parameters pc = sc.generate("Taiwan", exo);
        assertEquals(pc.getDouble("x2"), 2.0, 1e-5);
        Parameters ag = pc.breed("ag", "agent");
        assertEquals(ag.getDouble("x2"), 2.0, 1e-5);
    }
}
