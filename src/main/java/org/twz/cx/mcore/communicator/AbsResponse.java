package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

public abstract class AbsResponse implements IResponse, AdapterJSONObject, Cloneable {
    public AbsResponse() {
    }

    public AbsResponse(JSONObject js) {
    }

    public String getType() {
        return this.getClass().getSimpleName().replace("AbsResponse", "");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Type", getType());
        return js;
    }

    public abstract AbsResponse deepcopy();
}
