package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LeafY0 implements IY0 {
    private List<JSONObject> Definitions;

    public LeafY0() {
        Definitions = new ArrayList<>();
    }

    public LeafY0(JSONObject js) {
        Definitions = new ArrayList<>();
        JSONArray jar = js.getJSONArray("Definitions");
        for (int i = 0; i < jar.length(); i++) {
            Definitions.add(jar.getJSONObject(i));
        }
    }

    @Override
    public void matchModelInfo(AbsSimModel model) {

    }

    @Override
    public void append(JSONObject ent) {
        Definitions.add(ent);
    }

    @Override
    public void append(String ent) {
        append(new JSONObject(ent));
    }

    @Override
    public Collection<JSONObject> get() {
        return Definitions;
    }

    @Override
    public IY0 adaptTo(JSONObject src) {
        try {
            return this.getClass().getConstructor(JSONObject.class).newInstance(src);
        } catch (InstantiationException | IllegalAccessException |InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }
        return new LeafY0();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Definitions", Definitions);
        return js;
    }
}
