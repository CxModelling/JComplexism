package org.twz.cx.element.Ticker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class StepTicker extends AbsTicker {
    private double By;

    public StepTicker(double dt) {
        this("", dt);
    }

    public StepTicker(String name, Double dt) {
        super(name);
        By = dt;
    }

    public StepTicker(String name, Double dt, Double t) {
        super(name);
        By = dt;
        initialise(t);
    }

    @Override
    public double getNext() {
        return Last + By;
    }



    @Override
    JSONObject getArguments() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("dt", By);
        return js;
    }

    @Override
    String getType() {
        return "Clock";
    }
}
