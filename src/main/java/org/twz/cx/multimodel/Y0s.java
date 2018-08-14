package org.twz.cx.multimodel;


import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class Y0s implements IY0 {
    private Map<String, IY0> SubY0;
    private List<JSONObject> Definitions;

    public Y0s() {
        SubY0 = new HashMap<>();
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

    }

    public void addSubY0(String m, IY0 y0) {
        SubY0.put(m, y0);
    }

    @Override
    public Collection<JSONObject> get() {
        return Definitions;
    }

    public Map<String, IY0> getSubs() {
        return SubY0;
    }

    @Override
    public IY0 adaptTo(JSONArray src) {
        return null;
    }

    @Override
    public JSONArray toJSON() {
        return null;
    }
}
