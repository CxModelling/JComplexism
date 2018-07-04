package org.twz.cx.element.Ticker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class ScheduleTicker extends AbsTicker {
    private final List<Double> Ts;

    public ScheduleTicker(String name, List<Double> ts) {
        super(name);
        Ts = new ArrayList<>();
        Ts.addAll(ts);
    }

    public ScheduleTicker(String name, List<Double> ts, Double t) {
        this(name, ts);
        initialise(t);
    }

    @Override
    public double getNext() {
        for (double t: Ts) {
            if (t > Last)
                return t;
        }
        return Double.POSITIVE_INFINITY;
    }

    @Override
    JSONObject getArguments() {
        JSONObject js = new JSONObject();
        js.put("ts", Ts);
        return js;
    }

    @Override
    String getType() {
        return "Step";
    }
}
