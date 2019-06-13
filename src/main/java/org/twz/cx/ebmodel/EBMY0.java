package org.twz.cx.ebmodel;


import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.LeafY0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EBMY0 extends LeafY0 {
    @Override
    public void matchModelInfo(AbsSimModel model) {
        EquationBasedModel ebm = (EquationBasedModel) model;

        for (Map.Entry<String, Double> ent: ebm.getEquations().getDictY().entrySet()) {
            append(ent.getKey(), ent.getValue());
        }
    }

    public void append(String y, double x) {
        try {
            append("{'y':'" + y + "', 'n': "+x+"}");
        } catch (JSONException ignored) {

        }
    }

    public Map<String, Double> toMap() {
        Map<String, Double> m = new HashMap<>();
        for (JSONObject ent : getEntries()) {
            try {
                m.put(ent.getString("y"), ent.getDouble("n"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return m;
    }

}
