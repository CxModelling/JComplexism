package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dataframe.Pair;
import org.twz.io.FnJSON;

import java.util.HashMap;
import java.util.Map;

public class MultiValueShockResponse extends AbsResponse {
    private final String Target;
    private Map<String, String> ValueMap;

    public MultiValueShockResponse(String target, Map<String, String> vm) {
        Target = target;
        ValueMap = new HashMap<>(vm);
    }

    public MultiValueShockResponse(JSONObject js) throws JSONException {
        this(js.getString("Target"), FnJSON.toStringMap(js.getJSONObject("ValueMap")));
    }

    @Override
    public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) throws JSONException {
        JSONObject js = new JSONObject();
        for (Map.Entry<String, String> entry : ValueMap.entrySet()) {
            js.put(entry.getValue(), dis.get(entry.getKey()));
        }
        return new Pair<>(Target, js);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("ValueMap", ValueMap);
        js.put("Target", Target);
        return js;
    }

    @Override
    public AbsResponse deepcopy() {
        return new MultiValueShockResponse(Target, ValueMap);
    }
}
