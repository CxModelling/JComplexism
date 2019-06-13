package org.twz.fit.mcmc;

import org.twz.util.Statistics;

public class EffectiveSampleSize {


    public static int calculate(double[] xs) {
        int n = xs.length;
        xs = Statistics.add(xs, -Statistics.mean(xs));
        double v = Statistics.var(xs);

        double rho0 = getRho(xs, n, 1, v), rho1 = getRho(xs, n, 2, v), rs = rho0;
        int k = 2;

        while(k < n-1 & k < 1000) {
            k ++;
            rs += rho1;

            rho1 = getRho(xs, n, k, v);
        }
        rs = Math.max(rs, 0);
        return (int) Math.min(Math.round(n/(1+2*rs)), n);
    }

    private static double getRho(double[] xs, int n, int k, double v) {
        double vk = 0, d=0;
        for (int i = 0; i < n-k; i++) {
            vk += xs[i] * xs[i+k];
            d ++;
        }
        vk /= d;
        return vk/v;
    }
}
