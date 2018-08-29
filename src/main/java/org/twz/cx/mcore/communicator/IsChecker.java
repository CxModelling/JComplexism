package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class IsChecker extends AbsChecker {

    private String Message;

    public IsChecker(String msg) {
        Message = msg;
    }

    public IsChecker(JSONObject js) throws JSONException {
        Message = js.getString("Message");
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.equals(Message);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Message", Message);
        return js;
    }
}
