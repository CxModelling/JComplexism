package mcore.Ticker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class StepTicker extends AbsTicker {
    private final List<Double> Ts;

    public StepTicker(String name, List<Double> ts) {
        super(name);
        Ts = new ArrayList<>();
        Ts.addAll(ts);
    }

    public StepTicker(String name, List<Double> ts, Double t) {
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
