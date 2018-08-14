package org.twz.cx.ebmodel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.abmodel.ABMY0;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EBMY0 implements IY0 {
    private List<JSONObject> Definitions;

    public EBMY0() {
        Definitions = new ArrayList<>();
    }

    @Override
    public void matchModelInfo(AbsSimModel model) {
        EquationBasedModel ebm = (EquationBasedModel) model;
        List<String> ys = Definitions.stream().map(m->m.getString("y")).collect(Collectors.toList());
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
            Definitions.add(ent);
        }
    }

    @Override
    public void append(String ent) {
        append(new JSONObject(ent));
    }

    @Override
    public Collection<JSONObject> get() {
        return Definitions;
    }

    @Override
    public IY0 adaptTo(JSONArray src) {
        EBMY0 y0 = new EBMY0();
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
