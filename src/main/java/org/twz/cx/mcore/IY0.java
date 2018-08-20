package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

import java.util.Collection;

public interface IY0 extends Cloneable, AdapterJSONObject {
    void matchModelInfo(AbsSimModel model);
    void append(JSONObject ent);
    void append(String ent);
    Collection<JSONObject> get();
    IY0 adaptTo(JSONObject src);
    IY0 adaptTo(JSONArray src);
}
