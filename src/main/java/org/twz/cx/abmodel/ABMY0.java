package org.twz.cx.abmodel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ABMY0 implements IY0 {
    private List<JSONObject> Definitions;

    public ABMY0() {
        Definitions = new ArrayList<>();
    }

    @Override
    public void matchModelInfo(AbsSimModel model) {

    }

    @Override
    public void append(JSONObject ent) {
        if (ent.has("n") & ent.has("attributes")) {
            Definitions.add(ent);
        }
    }

    @Override
    public Collection<JSONObject> get() {
        return Definitions;
    }

    @Override
    public IY0 adaptTo(JSONArray src) {
        ABMY0 y0 = new ABMY0();
        for (int i = 0; i < src.length(); i++) {
            y0.append(src.getJSONObject(i));
        }
        return y0;
    }

    @Override
    public JSONArray toJSON() {
        return new JSONArray(Definitions);
    }
}
