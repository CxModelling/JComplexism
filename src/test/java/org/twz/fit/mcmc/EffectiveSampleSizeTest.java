package org.twz.fit.mcmc;

import org.junit.Before;
import org.junit.Test;
import org.twz.prob.Normal;


public class EffectiveSampleSizeTest {
    private double[] xs_iid, xs_ar1;

    @Before
    public void setUp() throws Exception {
        xs_iid = (new Normal(0, 0.1)).sample(1000);
        xs_ar1 = new double[1000];

        xs_ar1[0] = xs_iid[0];
        for (int i = 1; i < 1000; i++) {
            xs_ar1[i] = xs_ar1[i - 1] * 0.9 + xs_iid[i];
        }
    }

    @Test
    public void essIID() {
        System.out.println(EffectiveSampleSize.calculate(xs_iid));
    }

    @Test
    public void essAR1() {
        System.out.println(EffectiveSampleSize.calculate(xs_ar1));
    }
}
