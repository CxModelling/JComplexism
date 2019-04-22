package org.twz.cx.multimodel;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.ModelAtom;
import org.twz.cx.element.Request;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.BranchModel;
import org.twz.cx.mcore.BranchY0;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Parameters;

import java.util.HashMap;
import java.util.Map;

public class MultiModel extends BranchModel {
    private Map<String, AbsSimModel> Submodels;

    public MultiModel(String name, Parameters pars) {
        super(name, pars, new MMObserver(), new BranchY0());
        Submodels = new HashMap<>();
    }

    public MultiModel(String name, Map<String, Double> pars) {
        super(name, pars, new MMObserver(), new BranchY0());
        Submodels = new HashMap<>();
    }

    public void addObservingModel(String m) {
        assert Submodels.containsKey(m);
        ((MMObserver) Observer).addObservingModel(m);
    }

    @Override
    public Map<String, AbsSimModel> getModels() {
        return Submodels;
    }

    @Override
    public AbsSimModel getModel(String name) {
        return Submodels.get(name);
    }

    @Override
    public void appendModel(AbsSimModel mod) {
        Submodels.put(mod.getName(), mod);
    }

    @Override
    public ModelAtom getAtom(String atom) {
        return null;
    }

    @Override
    public void readY0(IY0 y0, double ti) throws JSONException {
        BranchY0 y0s = (BranchY0) y0;

        for (Map.Entry<String, AbsSimModel> entry : Submodels.entrySet()) {
            entry.getValue().readY0(y0s.getChildren(entry.getKey()), ti);
        }
    }

    @Override
    public void doRequest(Request req) {
        // todo
    }

    @Override
    public void shock(double time, String action, JSONObject value) {
        // todo
    }

    @Override
    public JSONObject toJSON() {
        return null; // todo
    }
}
