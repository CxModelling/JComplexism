package mcore.Ticker;

import org.json.JSONObject;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class ClockTicker extends AbsTicker {
    private double By;

    public ClockTicker(double dt) {
        this("", dt);
    }

    public ClockTicker(String name, Double dt) {
        super(name);
        By = dt;
    }

    public ClockTicker(String name, Double dt, Double t) {
        super(name);
        By = dt;
        initialise(t);
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

    @Override
    String getType() {
        return "Clock";
    }
}
