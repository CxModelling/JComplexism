package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.twz.cx.abmodel.ABMY0;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.mcore.IY0;
import org.twz.dag.ParameterCore;
import org.twz.io.FnJSON;
import org.twz.statespace.AbsStateSpace;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

import java.util.Map;

public class StSpABModel extends AbsAgentBasedModel<StSpAgent> {
    private AbsStateSpace DCore;

    public StSpABModel(String name, ParameterCore parameters, StSpPopulation pop) {
        super(name, parameters, pop, new StSpObserver(), new ABMY0());
        DCore = ((StSpBreeder) pop.getEva()).getDCore();
    }

    public StSpABModel(String name, Map<String, Double> parameters, StSpPopulation pop) {
        super(name, parameters, pop, new StSpObserver(), new ABMY0());
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

    public void addObservingBehaviour(String be) {
        addObservingBehaviour(Behaviours.get(be));
    }

    public void addObservingBehaviour(AbsBehaviour be) {
        ((StSpObserver) getObserver()).addObsBehaviour(be);
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

    @Override
    protected void record(AbsAgent ag, Object todo, double time) {
        ((StSpObserver) getObserver()).record(ag, (Transition) todo, time);
    }
}
