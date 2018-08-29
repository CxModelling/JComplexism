package org.twz.cx.element.Ticker;

import org.json.JSONException;
import org.twz.io.AdapterJSONObject;
import org.json.JSONObject;

/**
 * StepTicker object providing time to trigger an event
 * Created by TimeWz on 2017/10/12.
 */
public abstract class AbsTicker implements AdapterJSONObject {
    protected double Last;
    private final String Name;

    public AbsTicker(String name) {
        Name = name;
        Last = -1;
    }

    public void initialise(double ti) {
        Last = ti;
    }

    public abstract double getNext();

    public void update(double now) {
        while (now > Last) {
            Last = getNext();
        }
    }

    public String toString() {
        try {
            return toJSON().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    abstract JSONObject getArguments() throws JSONException;

    abstract String getType();

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject(), args = getArguments();
        args.put("t", Last);
        js.put("Type", getType());
        js.put("Args", args);
        return js;
    }

    public String getName() {
        return Name;
    }
}
