package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class StartWithChecker implements IChecker {

    private String Start;

    public StartWithChecker(String start) {
        Start = start;
    }

    public StartWithChecker(JSONObject js) {
        Start = js.getString("Start");
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.startsWith(Start);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Type", "StartWith");
        js.put("Start", Start);
        return js;
    }
}
