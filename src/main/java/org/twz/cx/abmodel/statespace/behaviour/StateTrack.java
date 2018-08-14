package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.PassiveBehaviour;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpAgent;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateTrigger;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.Map;

public class StateTrack extends PassiveBehaviour {
    private final State S_src;
    private double Value;

    public StateTrack(String name, State s_src) {
        super(name, new StateTrigger(s_src));
        S_src = s_src;
        Value = 0;
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        if (((StSpAgent) ag).isa(S_src)) {
            Value ++;
        } else {
            Value --;
        }
        model.disclose("update value to "+Value, getName());
    }

    @Override
    public void impulseEnter(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        Value ++;
        model.disclose("update value to "+Value, getName());
    }

    @Override
    public void impulseExit(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        Value --;
        model.disclose("update value to "+Value, getName());
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName(), Value);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        Value = ((StateTrack)be_src).Value;
    }

    @Override
    protected JSONObject getArgumentJSON() {
        JSONObject js = new JSONObject();
        js.put("s_src", S_src.getName());
        return js;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    private double evaluate(StSpABModel model) {
        return model.getPopulation().count("st", S_src);
    }

    @Override
    public String toString() {
        return String.format("StateTrack(%s, %s)", getName(), S_src.getName());
    }
}
