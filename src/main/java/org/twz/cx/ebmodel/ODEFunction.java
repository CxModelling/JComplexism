package org.twz.cx.ebmodel;

import org.json.JSONObject;
import org.twz.dag.Gene;

public interface ODEFunction {
    void call(double t, double[] y0, double[] y1, Gene parameters, JSONObject attributes);
}
