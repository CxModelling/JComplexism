package org.twz.dag;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BayesNetTest {

    private BayesNet bn1, bn2;

    @Before
    public void setUp() throws Exception {
        bn1 = new BayesNet("Main");
        bn1.appendLoci("Age ~ unif(10, 20)");
        bn1.appendLoci("y = Age * 20");
        bn1.complete();

        bn2 = new BayesNet("Sub");
        bn2.appendLoci("x ~ norm(y, 1)");
        bn2.complete();

    }

    @Test
    public void join() {
        System.out.println("Bayesian Network 1");
        System.out.println(bn1.toString());

        System.out.println("Bayesian Network 2");
        System.out.println(bn2.toString());

        bn1.join(bn2);
        System.out.println("The merged");
        System.out.println(bn1.toString());
    }

    @Test
    public void merge() {
        System.out.println("Bayesian Network 1");
        System.out.println(bn1.toString());

        System.out.println("Bayesian Network 2");
        System.out.println(bn2.toString());

        BayesNet merged = BayesNet.merge("Merged", bn1, bn2);
        System.out.println("The merged");
        System.out.println(merged.toString());
    }

}