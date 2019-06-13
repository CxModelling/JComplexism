package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.ABMY0;

import java.util.HashMap;
import java.util.Map;

public class StSpY0 extends ABMY0 {
    public void append(String st, int n, Map<String, Object> atr) throws JSONException {
        assert n > 0;
        JSONObject js = new JSONObject();
        atr = new HashMap<>(atr);
        atr.put("st", st);
        js.put("n", n);
        js.put("attributes", atr);
        append(js);
    }

    public void append(String st, int n) throws JSONException {
        assert n > 0;
        JSONObject js = new JSONObject();
        js.put("n", n);
        js.put("attributes", new JSONObject("{'st':"+ st + "}"));
        append(js);
    }
}
