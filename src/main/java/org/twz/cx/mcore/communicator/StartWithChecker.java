package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class StartWithChecker extends AbsChecker {

    private String Start;

    public StartWithChecker(String start) {
        Start = start;
    }

    public StartWithChecker(JSONObject js) throws JSONException {
        Start = js.getString("Start");
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.startsWith(Start);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Start", Start);
        return js;
    }

    @Override
    public AbsChecker deepcopy() {
        return new StartWithChecker(Start);
    }

    public StartWithChecker clone() {
        StartWithChecker checker;
        try {
            checker = (StartWithChecker) super.clone();
        } catch (CloneNotSupportedException e) {
            checker = new StartWithChecker(Start);
        }
        return checker;
    }
}
