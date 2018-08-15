package org.twz.cx.ebmodel;

import org.json.JSONObject;
import org.twz.dag.Gene;

import java.util.Map;

public interface EBMMeasurement {
    void call(Map<String, Double> tab, double ti, double[] ys, Gene pc, JSONObject x);
}
