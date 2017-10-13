package mcore.Ticker;

import org.json.JSONObject;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class ClockTicker extends AbsTicker {
    private double By;

    public ClockTicker(double dt) {
        super();
        By = dt;
    }

    @Override
    public double getNext() {
        return Last + By;
    }



    @Override
    JSONObject getArguments() {
        JSONObject js = new JSONObject();
        js.put("dt", By);
        return js;
    }
}
