package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateTrigger;
import org.twz.cx.abmodel.statespace.modifier.GloRateModifier;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

import java.util.Map;

public class DDShock extends PassiveModBehaviour {
    private final State S_src;
    private final Transition T_tar;
    private double Value;

    public DDShock(String name, State s_src, Transition t_tar) {
        super(name, new GloRateModifier(name, t_tar), new StateTrigger(s_src));
        S_src = s_src;
        T_tar = t_tar;
        Value = 0;
    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void impulseEnter(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void impulseExit(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName(), Value);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        Value = ((DDShock)be_src).Value;
        ags_new.values().forEach(ag->register(ag, ti));

    }

    @Override
    protected JSONObject getArgumentJSON() {
        JSONObject js = new JSONObject();
        js.put("s_src", S_src.getName());
        js.put("t_tar", T_tar.getName());
        return js;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    private double evaluate(StSpABModel model) {
        return model.getPopulation().count("st", S_src);
    }

    private void shock(StSpABModel model, double ti) {
        if (ModProto.update(Value)) {
            model.getPopulation().getAgents().values().forEach(ag->ag.modify(getName(), ti));
        }
    }

    @Override
    public String toString() {
        return String.format("FDShock(%s, %s on %s, by %s)", getName(), S_src.getName(), T_tar.getName(), Value);
    }
}
