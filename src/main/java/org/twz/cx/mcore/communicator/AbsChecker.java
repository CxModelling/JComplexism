package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

public abstract class AbsChecker implements IChecker, AdapterJSONObject {
    public AbsChecker() {
    }

    public AbsChecker(JSONObject js) {
    }

    public String getType() {
        return this.getClass().getSimpleName().replace("Checker", "");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Type", getType());
        return js;
    }
}
