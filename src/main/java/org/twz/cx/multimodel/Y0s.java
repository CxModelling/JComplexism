package org.twz.cx.multimodel;


import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.BranchY0;
import org.twz.cx.mcore.IY0;

import java.util.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class Y0s extends BranchY0 {
    private List<JSONObject> Definitions;

    public Y0s() {
        super();
        Definitions = new ArrayList<>();
    }

    @Override
    public void matchModelInfo(AbsSimModel model) {

    }

    @Override
    public void append(JSONObject ent) {
        Definitions.add(ent);
    }

    @Override
    public void append(String ent) {

    }

    @Override
    public Collection<JSONObject> get() {
        return Definitions;
    }

    @Override
    public IY0 adaptTo(JSONArray src) {
        return null;
    }

    @Override
    public JSONArray toJSON() {
        return null;
    }
}
