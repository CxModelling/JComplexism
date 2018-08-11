package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.twz.cx.abmodel.ABMY0;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.Population;
import org.twz.cx.mcore.AbsObserver;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Gene;
import org.twz.io.FnJSON;
import org.twz.statespace.AbsDCore;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

import java.util.Map;

public class StSpABModel extends AbsAgentBasedModel<StSpAgent> {
    private AbsDCore DCore;

    public StSpABModel(String name, Gene parameters, StSpPopulation pop, StSpObserver obs, ABMY0 protoY0) {
        super(name, parameters, pop, obs, protoY0);
        DCore = ((StSpBreeder) pop.getEva()).getDCore();
    }

    public StSpABModel(String name, Map<String, Double> parameters, StSpPopulation pop, StSpObserver obs, ABMY0 protoY0) {
        super(name, parameters, pop, obs, protoY0);
        DCore = ((StSpBreeder) pop.getEva()).getDCore();
    }

    public void addObservingTransition(String transition) {
        addObservingTransition(DCore.getTransition(transition));
    }

    public void addObservingTransition(Transition transition) {
        ((StSpObserver) getObserver()).addObsTransition(transition);
    }

    public void addObservingState(String state) {
        addObservingState(DCore.getState(state));
    }

    public void addObservingState(State state) {
        ((StSpObserver) getObserver()).addObsState(state);
    }

    @Override
    public void readY0(IY0 y0, double ti) {
        for (JSONObject y : y0.get()) {
            Map<String, Object> atr = FnJSON.toObjectMap(y.getJSONObject("attributes"));
            makeAgents(y.getInt("n"), ti, atr);
        }
    }

    @Override
    public Double getSnapshot(String key, double ti) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
