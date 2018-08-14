package org.twz.cx.multimodel;

import org.json.JSONObject;
import org.twz.cx.element.Request;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.BranchModel;
import org.twz.cx.mcore.IY0;
import org.twz.dag.ParameterCore;

import java.util.HashMap;
import java.util.Map;

public class MultiModel extends BranchModel {
    private Map<String, AbsSimModel> Submodels;

    public MultiModel(String name, ParameterCore pars) {
        super(name, pars, new MMObserver(), new Y0s());
        Submodels = new HashMap<>();
    }

    public MultiModel(String name, Map<String, Double> pars) {
        super(name, pars, new MMObserver(), new Y0s());
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
    public void readY0(IY0 y0, double ti) {
        Y0s y0s = (Y0s) y0;
        Map<String, IY0> subs = y0s.getSubs();
        for (Map.Entry<String, AbsSimModel> entry : Submodels.entrySet()) {
            entry.getValue().readY0(subs.get(entry.getKey()), ti);
        }
    }

    @Override
    public void doRequest(Request req) {

    }

    @Override
    public void shock(double time, AbsSimModel model, String action, JSONObject value) {

    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
