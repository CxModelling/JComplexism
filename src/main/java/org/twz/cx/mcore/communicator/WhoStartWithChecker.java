package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class WhoStartWithChecker extends AbsChecker {

    private final String Who;
    private final String Start;

    public WhoStartWithChecker(String who, String start) {
        Who = who;
        Start = start;
    }

    public WhoStartWithChecker(JSONObject js) throws JSONException {
        Who = js.getString("Who");
        Start = js.getString("Start");
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.startsWith(Start) && dis.Who.equals(Who);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Start", Start);
        js.put("Who", Who);
        return js;
    }

    @Override
    public AbsChecker deepcopy() {
        return new WhoStartWithChecker(Who, Start);
    }

}
