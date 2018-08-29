package org.twz.io;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by TimeWz on 2017/10/10.
 */
public interface AdapterJSONArray {
    JSONArray toJSON() throws JSONException;
}
