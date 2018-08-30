package org.twz.cx.mcore.communicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dataframe.Pair;
import org.twz.io.AdapterJSONArray;
import org.twz.io.AdapterJSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ListenerSet implements AdapterJSONArray {
    private Map<IChecker, IResponse> Listeners;

    public ListenerSet() {
        Listeners = new LinkedHashMap<>();
    }

    public ListenerSet(JSONArray js) throws JSONException {
        this();
        JSONObject temp;
        for (int i = 0; i < js.length(); i++) {
            temp = js.getJSONObject(i);
            defineImpulseResponse(
                    findChecker(temp.getJSONObject("Impulse")),
                    findShocker(temp.getJSONObject("Response")));
        }
    }

    public void defineImpulseResponse(IChecker impulse, IResponse response) {
        Listeners.put(impulse, response);

    }

    public boolean applyShock(Disclosure disclosure, AbsSimModel foreign, AbsSimModel local, double ti) throws JSONException {
        boolean shock = false;
        Pair<String, JSONObject> action;
        for (Map.Entry<IChecker, IResponse> entry : Listeners.entrySet()) {
            if (entry.getKey().check(disclosure)) {
                action = entry.getValue().shock(disclosure, foreign, local, ti);
                local.shock(ti, action.getFirst(), action.getValue());
                shock = true;
            }
        }
        return shock;
    }

    public Set<IChecker> getAllCheckers() {
        return Listeners.keySet();
    }


    @Override
    public JSONArray toJSON() throws JSONException {
        JSONArray js = new JSONArray();
        JSONObject temp;
        for (Map.Entry<IChecker, IResponse> entry : Listeners.entrySet()) {
            temp = new JSONObject();
            temp.put("Impulse", ((AdapterJSONObject) entry.getKey()).toJSON());
            temp.put("Response", ((AdapterJSONObject) entry.getValue()).toJSON());
            js.put(temp);
        }
        return js;
    }

    private static IChecker findChecker(JSONObject js) throws JSONException {
        switch (js.getString("Type")) {
            case "StartWith":
                return new StartWithChecker(js);

            default:
                return null;
        }
    }

    private static IResponse findShocker(JSONObject js) {
        return null; //todo
    }
}
