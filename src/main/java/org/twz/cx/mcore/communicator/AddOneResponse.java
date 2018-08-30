package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dataframe.Pair;

public class AddOneResponse extends AbsResponse {
    private String Target;

    public AddOneResponse(String target) {
        Target = target;
    }

    public AddOneResponse(JSONObject js) throws JSONException {
        this(js.getString("Target"));
    }

    @Override
    public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) throws JSONException {
        JSONObject js = new JSONObject();
        js.put("y", Target);
        js.put("n", 1);
        return new Pair<>("add", js);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Target", Target);
        return js;
    }

    @Override
    public AbsResponse deepcopy() {
        return new AddOneResponse(Target);
    }
}
