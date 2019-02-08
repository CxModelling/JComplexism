package org.twz.cx.ebmodel;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.element.ModelAtom;
import org.twz.cx.element.Request;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.LeafModel;
import org.twz.dag.ParameterCore;

import java.util.HashMap;
import java.util.Map;

public class EquationBasedModel extends LeafModel {
    private AbsEquations Equations;
    private Map<String, Double> Y;

    public EquationBasedModel(String name, AbsEquations eqs, ParameterCore pars) {
        this(name, eqs, pars, new EBMObserver(), new EBMY0());
    }

    public EquationBasedModel(String name, AbsEquations eqs, Map<String, Double> pars) {
        this(name, eqs, pars, new EBMObserver(), new EBMY0());
    }

    private EquationBasedModel(String name, AbsEquations eqs, ParameterCore pars, EBMObserver obs, IY0 protoY0) {
        super(name, pars, obs, protoY0);
        Equations = eqs;
        Y = new HashMap<>();
        Scheduler.addAtom(eqs);
    }

    private EquationBasedModel(String name, AbsEquations eqs, Map<String, Double> pars, EBMObserver obs, IY0 protoY0) {
        super(name, pars, obs, protoY0);
        Equations = eqs;
        Y = new HashMap<>();
        Scheduler.addAtom(eqs);
    }

    public void addObservingStock(String stock) {
        ((EBMObserver) getObserver()).addObservingStock(stock);
    }

    public void addObservingStockFunction(EBMMeasurement fn) {
        ((EBMObserver) getObserver()).addObservingStockFunction(fn);
    }

    public void addObservingFlowFunction(EBMMeasurement fn) {
        ((EBMObserver) getObserver()).addObservingFlowFunction(fn);
    }

    public AbsEquations getEquations() {
        return Equations;
    }

    public String[] getYNames() {
        return Equations.getYNames();
    }

    @Override
    public void readY0(IY0 y0, double ti) throws JSONException {
        Map<String, Double> m = new HashMap<>();
        for (JSONObject ent : y0.getEntries()) {
            m.put(ent.getString("y"), ent.getDouble("n"));
        }
        Equations.setY(m);
    }

    @Override
    public ModelAtom getAtom(String atom) {
        if (atom.equals("Equations")) {
            return Equations;
        }
        return null;
    }

    @Override
    public void preset(double ti) {
        Equations.setY(Y);
        Equations.initialise(ti, this);
        super.preset(ti);
    }

    @Override
    public void reset(double ti) {
        Equations.reset(ti, this);
        Equations.setY(Y);
        super.reset(ti);
    }

    @Override
    public void doRequest(Request req) {
        Equations.executeEvent();
    }

    @Override
    public void shock(double ti, String action, JSONObject value) throws JSONException {
        Equations.shock(ti, this, action, value);
        Y = Equations.getDictY();
    }

    @Override
    public Double getSnapshot(String key, double ti) {
        return getObserver().getSnapshot(this, key, ti);
    }

    public void goTo(double ti) {
        Equations.goTo(ti);
        Y = Equations.getDictY();
    }

    @Override
    public void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) throws JSONException {
        Equations.goTo(ti);
        super.fetchDisclosures(ds_ms, ti);
    }

    public void measure(Map<String, Double> tab, EBMMeasurement measurement) {
        Equations.measure(tab, measurement);
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
