package org.twz.cx.ebmodel;


import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.BranchY0;

import java.util.ArrayList;
import java.util.List;

public class EBMY0 extends BranchY0 {
    @Override
    public void matchModelInfo(AbsSimModel model) throws JSONException {
        org.twz.cx.ebmodel.EquationBasedModel ebm = (EquationBasedModel) model;
        List<String> ys = new ArrayList<>();
        for (JSONObject m : Entries) {
            String y = m.getString("y");
            ys.add(y);
        }
        String[] yns = ebm.getYNames();
        for (String yn : yns) {
            if (!ys.contains(yn)) {
                append("{'y':'" + yn + "', 'n': 0}");
            }
        }
    }

    @Override
    public void append(JSONObject ent) {
        if (ent.has("n") && ent.has("y")) {
            Entries.add(ent);
        }
    }

}
