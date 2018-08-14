package org.twz.cx.ebmodel;

import org.twz.dag.Gene;

import java.util.Map;

public interface EBMMeasurement {
    void call(Map<String, Double> tab, double ti, double[] ys, Gene pc, Map<String, Double> x);
}
