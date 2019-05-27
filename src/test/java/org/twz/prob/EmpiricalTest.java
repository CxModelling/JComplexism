package org.twz.prob;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmpiricalTest {

    private Empirical Dist;

    @Before
    public void setUp() throws Exception {
        double[] xs = new double[]{1, 3, 4, 7, 9};
        double[] ps = new double[]{0, 0.2, 0.4, 0.7, 1.0};

        Dist = new Empirical("Test", ps, xs);
    }

    @Test
    public void logProb() {
        assertEquals(Dist.logProb(7), -2.079441541540058, 1e-6);
    }

    @Test
    public void sample() {
        System.out.println(Dist.sample());
    }
}