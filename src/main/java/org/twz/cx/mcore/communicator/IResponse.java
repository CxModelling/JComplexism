package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dataframe.Pair;
import org.twz.io.AdapterJSONObject;

public interface IResponse {
    Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) throws JSONException;
}
