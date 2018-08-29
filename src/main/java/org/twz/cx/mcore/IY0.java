package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

import java.util.Collection;

public interface IY0 extends Cloneable, AdapterJSONObject {
    void matchModelInfo(AbsSimModel model) throws JSONException;
    void append(JSONObject ent);
    void append(String ent) throws JSONException;
    Collection<JSONObject> getEntries();
    IY0 adaptTo(JSONObject src);
    IY0 adaptTo(JSONArray src);
}
