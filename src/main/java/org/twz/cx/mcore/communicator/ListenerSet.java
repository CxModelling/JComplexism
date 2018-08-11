package org.twz.cx.mcore.communicator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.io.AdapterJSONArray;
import org.twz.io.AdapterJSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ListenerSet implements AdapterJSONArray {
    private Map<IChecker, IShocker> Listeners;

    public ListenerSet() {
        Listeners = new LinkedHashMap<>();
    }

    public ListenerSet(JSONArray js) {
        this();
        JSONObject temp;
        for (int i = 0; i < js.length(); i++) {
            temp = js.getJSONObject(i);
            defineImpulseResponse(
                    findChecker(temp.getJSONObject("Impulse")),
                    findShocker(temp.getJSONObject("Response")));
        }
    }

    public void defineImpulseResponse(IChecker impulse, IShocker response) {
        Listeners.put(impulse, response);

    }

    public boolean applyShock(Disclosure disclosure, AbsSimModel foreign, AbsSimModel local, double ti) {
        boolean shock = false;
        for (Map.Entry<IChecker, IShocker> entry : Listeners.entrySet()) {
            if (entry.getKey().check(disclosure)) {
                entry.getValue().shock(disclosure, foreign, local, ti);
                shock = true;
            }
        }
        return shock;
    }

    public Set<IChecker> getAllCheckers() {
        return Listeners.keySet();
    }


    @Override
    public JSONArray toJSON() {
        JSONArray js = new JSONArray();
        JSONObject temp;
        for (Map.Entry<IChecker, IShocker> entry : Listeners.entrySet()) {
            temp = new JSONObject();
            temp.put("Impulse", entry.getKey().toJSON());
            temp.put("Response", entry.getValue().toJSON());
            js.put(temp);
        }
        return js;
    }

    private static IChecker findChecker(JSONObject js) {
        switch (js.getString("Type")) {
            case "StartWith":
                return new StartWithChecker(js);

            default:
                return null;
        }
    }

    private static IShocker findShocker(JSONObject js) {
        return null; //todo
    }
}
