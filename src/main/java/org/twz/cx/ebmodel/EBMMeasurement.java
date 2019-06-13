package org.twz.cx.ebmodel;

import org.twz.dag.Chromosome;

import java.util.Map;

public interface EBMMeasurement {
    void call(Map<String, Double> tab, double ti, double[] ys, Chromosome pars, Map<String, Object> x);
}
