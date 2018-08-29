package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class HaveStringChecker extends AbsChecker {

    private String Have;

    public HaveStringChecker(String have) {
        Have = have;
    }

    public HaveStringChecker(JSONObject js) throws JSONException {
        Have = js.getString("Have");
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.contains(Have);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Have", Have);
        return js;
    }
}
