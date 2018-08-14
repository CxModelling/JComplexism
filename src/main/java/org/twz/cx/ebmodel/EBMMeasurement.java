package org.twz.cx.ebmodel;

import java.util.Map;

public interface EBMMeasurement {
    void call(Map<String, Double> tab, double ti, double[] ys, );
}
