package org.twz.cx.ebmodel;

import org.twz.dag.Chromosome;

import java.util.Map;

public interface ODEFunction {
    void call(double t, double[] y0, double[] y1, Chromosome parameters, Map<String, Object> attributes);
}
