package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dataframe.Pair;

public class ValueShockResponse extends AbsResponse {
    private String Target;
    private String Value;

    public ValueShockResponse(String target, String value) {
        Target = target;
        Value = value;
    }

    public ValueShockResponse(String target) {
        this(target, "v1");
    }

    public ValueShockResponse(JSONObject js) throws JSONException {
        this(js.getString("Target"), js.getString("Value"));
    }

    @Override
    public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) throws JSONException {
        JSONObject js = new JSONObject();
        js.put("v", dis.getDouble(this.Value));
        return new Pair<>(Target, js);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Value", Value);
        js.put("Target", Target);
        return js;
    }

    @Override
    public AbsResponse deepcopy() {
        return new ValueShockResponse(Target, Value);
    }
}
