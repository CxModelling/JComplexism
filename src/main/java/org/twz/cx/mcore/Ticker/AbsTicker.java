package org.twz.cx.mcore.Ticker;

import org.twz.io.AdapterJSONObject;
import org.json.JSONObject;

/**
 * ClockTicker object providing time to trigger an event
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
        return toJSON().toString();
    }

    abstract JSONObject getArguments();

    abstract String getType();

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject(), args = getArguments();
        args.put("t", Last);
        js.put("Type", getType());
        js.put("Args", args);
        return js;
    }
}
