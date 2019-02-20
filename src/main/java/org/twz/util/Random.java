package org.twz.util;

import org.twz.prob.Sample;

import java.util.ArrayList;
import java.util.List;

public class Random {
    public static int[] sample(double[] prob, int n) {
        Sample s = new Sample(prob);
        return s.sample(n);
    }

    public static int sample(double[] prob) {
        Sample s = new Sample(prob);
        return s.sample();
    }

    public static <T> List<T> sample(List<T> objs, double[] prob) {
        int[] ss = sample(prob, prob.length);
        List<T> Sampled = new ArrayList<T>();
        for (int i: ss) {
            Sampled.add(objs.get(i));
        }
        return Sampled;
    }

    public static int sampleN(int n) {
        return (int) (Math.random() * n);
    }
}
