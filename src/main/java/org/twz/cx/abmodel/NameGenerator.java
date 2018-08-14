package org.twz.cx.abmodel;

import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

class NameGenerator implements Cloneable, AdapterJSONObject {
    private String Prefix;
    private int Ini, Step;

    public NameGenerator(String prefix, int ini, int step) {
        Prefix = prefix;
        Ini = ini;
        Step = step;
    }

    public NameGenerator(String prefix) {
        this(prefix, 1, 1);
    }

    public NameGenerator(JSONObject js) {
        this(js.getString("Prefix"), js.getInt("Ini"), js.getInt("Step"));
    }

    public NameGenerator() {
        this("Ag");
    }

    public String getNext() {
        int next = Ini;
        Ini += Step;
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

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Prefix", Prefix);
        js.put("Ini", Ini);
        js.put("Step", Step);
        return js;
    }
}
