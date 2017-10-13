package mcore.Ticker;

import org.json.JSONObject;
import java.util.List;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class StepTicker extends AbsTicker {
    private final List<Double> Ts;


    public StepTicker(List<Double> ts) {
        super();
        Ts = ts;
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
}
