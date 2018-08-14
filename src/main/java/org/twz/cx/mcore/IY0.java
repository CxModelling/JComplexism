package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.io.AdapterJSONArray;

import java.util.Collection;

public interface IY0 extends Cloneable, AdapterJSONArray {
    void matchModelInfo(AbsSimModel model);
    void append(JSONObject ent);
    void append(String ent);
    Collection<JSONObject> get();
    IY0 adaptTo(JSONArray src);
}
