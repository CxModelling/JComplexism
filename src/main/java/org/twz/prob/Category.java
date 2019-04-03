package org.twz.prob;


import org.twz.exception.IncompleteConditionException;

public class Category implements IDistribution {
    private String Name;
    private double[] Prob;
    private Sample Samp;

    public Category(String name, double[] prob) {
        Name = name;
        Prob = prob;
        Samp = new Sample(Prob);
    }

    public Category(double[] prob) {
        this("Cat(.)", prob);
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getDataType() {
        return "Categorical";
    }

    @Override
    public double logProb(double rv) {
        return Math.log(Prob[(int) rv]);
    }

    @Override
    public double sample() {
        return Samp.sample();
    }

    @Override
    public double[] sample(int n) {
        int[] xs = Samp.sample(n);
        double[] ys = new double[n];

        for (int i = 0; i < xs.length; i++) {
            ys[i] = xs[i];
        }

        return ys;
    }
}
