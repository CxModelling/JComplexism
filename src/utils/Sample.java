package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2016/6/1.
 */
public class Sample {
    private double[] cum;
    private int[] domain;
    private double max;
    private int n;

    public Sample(Map<Integer, Double> prob) {
        cum = new double[prob.size()];
        domain = new int[prob.size()];
        max = 0;
        int i = 0;
        for (Map.Entry<Integer, Double> e: prob.entrySet()) {
            max += e.getValue();
            cum[i] = max;
            domain[i] = e.getKey();
            i ++;
        }
        n = prob.size();
    }

    public Sample(int[] val, double[] prob){
        n = Math.min(val.length, prob.length);

        cum = new double[n];
        domain = new int[n];
        max = 0;
        for  (int i = 0; i < n; i++) {
            max += prob[i];
            cum[i] = max;
            domain[i] = val[i];
        }
    }

    public Sample(double[] prob){
        this(prob, 0);
    }

    public Sample(double[] prob, int k){
        n = prob.length;

        cum = new double[n];
        domain = new int[n];
        max = 0;
        for  (int i = 0; i < n; i++) {
            max += prob[i];
            cum[i] = max;
            domain[i] = i + k;
        }
    }

    public int sample() {
        double rand =  Math.random()*max;
        for(int i = 0; i < n ; i++){
            if(rand < cum[i])
                return domain[i];
        }
        return domain[n-1];
    }

    public int[] sample(int Size){
        int[] sam = new int[Size];
        for (int i = 0; i < Size; i++) {
            sam[i] = sample();
        }
        return sam;
    }


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

