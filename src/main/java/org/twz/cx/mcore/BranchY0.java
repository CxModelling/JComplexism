package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BranchY0 implements IY0 {
    private Map<String, IY0> Children;
    protected    List<JSONObject> Entries;

    public BranchY0() {
        Children = new HashMap<>();
        Entries = new ArrayList<>();
    }

    public BranchY0(JSONObject js) throws JSONException {
        this(js.getJSONArray("Entries"), js.getJSONObject("Children"));
    }

    public BranchY0(JSONArray jar, JSONObject js) throws JSONException {
        this();
        for (int i = 0; i < jar.length(); i++) {
            Entries.add(jar.getJSONObject(i));
        }
        String[] keys = JSONObject.getNames(js);
        JSONObject ent;
        for (String key : keys) {
            ent = js.getJSONObject(key);
            if (ent.getString("Type").equals("Leaf")) {
                Children.put(key, new LeafY0(ent));
            } else {
                Children.put(key, new BranchY0(ent));
            }

        }
    }

    public BranchY0(JSONArray jar) throws JSONException {
        this(jar, new JSONObject());
    }

    public IY0 getChildren(String key) {
        return Children.get(key);
    }

    public void appendChildren(String key, IY0 chd) {
        Children.put(key, chd);
    }

    @Override
    public void matchModelInfo(AbsSimModel model) throws JSONException {

    }

    @Override
    public void append(JSONObject ent) {
        Entries.add(ent);
    }

    @Override
    public void append(String ent) throws JSONException {
        append(new JSONObject(ent));
    }

    @Override
    public Collection<JSONObject> getEntries() {
        return Entries;
    }

    @Override
    public IY0 adaptTo(JSONObject src) {
        try {
            return this.getClass().getConstructor(JSONObject.class).newInstance(src);
        } catch (InstantiationException | IllegalAccessException |InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }
        return new BranchY0();
    }

    @Override
    public IY0 adaptTo(JSONArray src) {
        try {
            return this.getClass().getConstructor(JSONArray.class).newInstance(src);
        } catch (InstantiationException | IllegalAccessException |InvocationTargetException | NoSuchMethodException  e) {
            e.printStackTrace();
        }
        return new BranchY0();
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Entries", Entries);
        js.put("Type", "Branch");
        js.put("Children", Children);
        return js;
    }
}
