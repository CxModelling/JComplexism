package org.twz.dag;

import org.junit.Before;
import org.junit.Test;
import org.twz.dag.BayesNet;
import org.twz.dag.util.NodeGroup;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNetHierarchyTest {

    private BayesNet BN;
    private NodeGroup NG;


    @Before
    public void setUp() throws Exception {
        BN = new BayesNet("test");
        BN.appendLoci("b1 = 0.5");
        BN.appendLoci("b0 ~ norm(12, 1)");
        BN.appendLoci("b0r ~ norm(0, .01)");
        BN.appendLoci("pf ~ beta(8, 20)");
        BN.appendLoci("foodstore ~ binom(100, pf)");
        BN.appendLoci("ageA ~ unif(0, 100)");
        BN.appendLoci("ageB ~ unif(0, 100)");
        BN.appendLoci("muA = b0 + b0r + b1*ageA");
        BN.appendLoci("muB = b0 + b0r + b1*ageB");
        BN.appendLoci("sdB = sd * 0.5");
        BN.appendLoci("bmiA ~ norm(muA, sd)");
        BN.appendLoci("bmiB ~ norm(muB, sdB)");


        NG = new NodeGroup("country", new String[]{"b1"});

        NodeGroup city = new NodeGroup("city", new String[]{"b0r", "pf"});
        NG.appendChildren(city);

        city.appendChildren(new NodeGroup("agA", new String[]{"ageA"}));
        city.appendChildren(new NodeGroup("agB", new String[]{"ageB"}));

    }

    @Test
    public void formHei() throws Exception {
        System.out.println("---------- Predefined nodes ----------");
        NG.print();
        NG.form_hierarchy(BN);
        System.out.println("---------- Hierarchy check -----------");
        NG.print();
        System.out.println("--------------------------------------");
        NG.analyseTypes(BN);
        System.out.println("--------------------------------------");
        NG.allocateNodes(BN);
        NG.printBlueprint();
    }


}
