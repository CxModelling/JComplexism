package org.twz.dag.util;

import org.junit.Before;
import org.junit.Test;

import org.twz.dag.BayesNet;
import org.twz.dag.NodeSet;
import org.twz.exception.ValidationException;

public class NodeSetTest {

    private BayesNet BN1;

    @Before
    public void setUp() {
        BN1 = new BayesNet("Test1");
        BN1.appendLoci("Region");
        BN1.appendLoci("Age = 15");
        BN1.appendLoci("Rain ~ binom(0.5, 1)");
        BN1.appendLoci("beta = 0.5");
        BN1.appendLoci("mu = 0.5 * Region + beta*Age + Rain");
        BN1.appendLoci("x ~ norm(mu, 0.01)");
    }

    @Test
    public void collectMinimal() throws ValidationException {
        NodeSet NS = new NodeSet("Area", new String[]{});
        NS.appendChild(
                new NodeSet("Agent", new String[]{"Age", "x"}));

        BN1.print();
        NS.injectGraph(BN1);
        NS.print();
    }
}