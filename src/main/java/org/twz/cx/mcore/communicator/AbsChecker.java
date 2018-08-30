package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

public abstract class AbsChecker implements IChecker, AdapterJSONObject {
    AbsChecker() {
    }

    public AbsChecker(JSONObject js) {
    }

    public String getType() {
        return this.getClass().getSimpleName().replace("Checker", "");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Type", getType());
        return js;
    }

    public abstract AbsChecker deepcopy();
}
