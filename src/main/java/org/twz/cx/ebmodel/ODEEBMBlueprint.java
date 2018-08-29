package org.twz.cx.ebmodel;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.Director;
import org.twz.cx.mcore.IModelBlueprint;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.io.FnJSON;
import org.twz.statespace.AbsStateSpace;
import org.twz.statespace.IStateSpaceBlueprint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ODEEBMBlueprint implements IModelBlueprint<EquationBasedModel> {
    private final String Name;
    private double Dt;
    private ODEFunction Fn;
    private String[] Ys, Ps;
    private JSONObject Xs;
    private List<String> ObsYs;
    private List<EBMMeasurement> Measurements;

    public ODEEBMBlueprint(String name) {
        Name = name;
        Dt = 1;
        Xs = new JSONObject();
        ObsYs = new ArrayList<>();
        Ps = new String[0];
        Measurements = new ArrayList<>();
    }

    @Override
    public String getName() {
        return Name;
    }

    public void setODE(ODEFunction fn, String[] ys) {
        Fn = fn;
        Ys = ys;
    }

    public void setDt(double dt) {
        assert dt > 0;
        Dt = dt;
    }

    public void setRequiredParameters(String[] ps) {
        Ps = ps;
    }

    public void setExternalVariables(JSONObject js) {
        Xs = js;
    }

    public void appendExternalVariable(String k, Object o) throws JSONException {
        Xs.put(k, o);
    }

    public void setObservations(String[] states) {
        try {
            ObsYs = Arrays.asList(states);
        } catch (NullPointerException ignored) {

        }
    }

    public void addMeasurementFunction(EBMMeasurement m) {
        Measurements.add(m);
    }

    @Override
    public NodeGroup getParameterHierarchy(Director dc) {
        return new NodeGroup(getName(), Ps);
    }

    @Override
    public EquationBasedModel generate(String name, Map<String, Object> args) throws JSONException {
        ParameterCore pc;

        if (args.containsKey("bn") && args.containsKey("da")) {
            Director da = (Director) args.get("da");
            pc = da.getBayesNet((String) args.get("bn")).toSimulationCore(getParameterHierarchy(da), true).generate(name);

        } else if(args.containsKey("pc")) {
            pc = (ParameterCore) args.get("pc");
        } else {
            assert false;
            return null;
        }

        ODEquations eq = new ODEquations(name, Fn, Ys, Dt, pc);
        eq.updateAttributes(FnJSON.toObjectMap(Xs));
        EquationBasedModel ebm = new EquationBasedModel(name, eq, pc);

        ObsYs.forEach(ebm::addObservingStock);
        Measurements.forEach(ebm::addObservingStockFunction);

        return ebm;
    }

    @Override
    public boolean isWellDefined() {
        if (Fn == null) return false;
        if (Ys.length == 0) return false;
        return ObsYs.size() + Measurements.size() > 0;
    }
}
