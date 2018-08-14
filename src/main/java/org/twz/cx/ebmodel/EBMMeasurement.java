package org.twz.cx.ebmodel;

import org.twz.dag.ParameterCore;

import java.util.Map;

public interface EBMMeasurement {
    void call(Map<String, Double> tab, double ti, double[] ys, ParameterCore pc, Map<String, Object> x);
}
