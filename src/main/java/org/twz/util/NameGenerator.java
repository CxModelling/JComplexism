package org.twz.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

public class NameGenerator implements Cloneable, AdapterJSONObject {
    private String Prefix;
    private int Initial, Current, Step;

    public NameGenerator(String prefix, int ini, int step) {
        Prefix = prefix;
        Initial = ini;
        Current = ini;
        Step = step;
    }

    public NameGenerator(String prefix) {
        this(prefix, 1, 1);
    }

    public NameGenerator(JSONObject js) throws JSONException {
        this(js.getString("Prefix"), js.getInt("Ini"), js.getInt("Step"));
    }

    public NameGenerator() {
        this("Ag");
    }

    public String getNext() {
        int next = Current;
        Current += Step;
        return Prefix + next;
    }

    public NameGenerator clone() {
        NameGenerator ng = null;
        try {
            ng = (NameGenerator) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ng;
    }

    public void reset() {
        Current = Initial;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Prefix", Prefix);
        js.put("Ini", Initial);
        js.put("Step", Step);
        return js;
    }
}
