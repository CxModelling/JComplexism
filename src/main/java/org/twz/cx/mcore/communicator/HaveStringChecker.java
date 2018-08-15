package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class HaveStringChecker implements IChecker {

    private String Have;

    public HaveStringChecker(String have) {
        Have = have;
    }

    public HaveStringChecker(JSONObject js) {
        Have = js.getString("Have");
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.contains(Have);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Type", "HaveString");
        js.put("Have", Have);
        return js;
    }
}
