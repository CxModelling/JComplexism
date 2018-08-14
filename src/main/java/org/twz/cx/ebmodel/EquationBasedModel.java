package org.twz.cx.ebmodel;

import org.json.JSONObject;
import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IObsFun;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.LeafModel;
import org.twz.dag.Gene;
import org.twz.dag.ParameterCore;

import java.util.HashMap;
import java.util.Map;

public class EquationBasedModel extends LeafModel {
    private AbsEquations Equations;
    private Map<String, Double> Y;

    public EquationBasedModel(String name, AbsEquations eqs, ParameterCore pars, IY0 protoY0) {
        this(name, eqs, pars, new EBMObserver(), protoY0);
    }

    public EquationBasedModel(String name, AbsEquations eqs, Map<String, Double> pars, IY0 protoY0) {
        this(name, eqs, pars, new EBMObserver(), protoY0);
    }

    public EquationBasedModel(String name, AbsEquations eqs, ParameterCore pars, EBMObserver obs, IY0 protoY0) {
        super(name, pars, obs, protoY0);
        Equations = eqs;
        Y = new HashMap<>();
        Scheduler.addAtom(eqs);
    }

    public EquationBasedModel(String name, AbsEquations eqs, Map<String, Double> pars, EBMObserver obs, IY0 protoY0) {
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
    public void readY0(IY0 y0, double ti) {
        Map<String, Double> m = new HashMap<>();
        for (JSONObject ent : y0.get()) {
            m.put(ent.getString("y"), ent.getDouble("n"));
        }
        Equations.setY(m);
    }

    @Override
    public void preset(double ti) {
        Equations.setY(Y);
        Equations.initialise(ti, this);
        disclose("initialise", "*");
        Scheduler.rescheduleAllAtoms();
    }

    @Override
    public void reset(double ti) {
        Equations.reset(ti, this);
        Equations.setY(Y);
        disclose("initialise", "*");
        Scheduler.rescheduleAllAtoms();
    }

    @Override
    public void doRequest(Request req) {
        Equations.executeEvent();
    }

    @Override
    public void shock(double ti, AbsSimModel model, String action, JSONObject value) {
        Equations.shock(ti, model, action, value);
        Y = Equations.getDictY();
    }

    @Override
    public Double getSnapshot(String key, double ti) {
        return null;
    }

    public void goTo(double ti) {
        Equations.updateTo(ti);
        Y = Equations.getDictY();
    }

    @Override
    public void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) {
        Equations.updateTo(ti);
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
