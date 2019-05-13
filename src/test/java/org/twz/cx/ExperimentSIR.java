package org.twz.cx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.mcore.IY0;

public class ExperimentSIR extends Experiment {
    public ExperimentSIR(Director ctrl, String bn, String simModel, double t0, double t1, double dt) {
        super(ctrl, bn, simModel, t0, t1, dt);
    }

    @Override
    protected IY0 translateY0(JSONObject js) {
        EBMY0 y0 = new EBMY0();

        try {
            JSONArray entries = js.getJSONArray("Entries");
            for (int i = 0; i < entries.length(); i++) {
                y0.append(entries.getJSONObject(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return y0;
    }
}
