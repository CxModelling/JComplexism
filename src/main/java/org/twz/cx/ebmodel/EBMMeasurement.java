package org.twz.cx.ebmodel;

import org.twz.dag.Chromosome;

import java.util.Map;

public interface EBMMeasurement {
    void call(Map<String, Double> tab, double ti, double[] ys, Chromosome pc, Map<String, Object> x);
}
