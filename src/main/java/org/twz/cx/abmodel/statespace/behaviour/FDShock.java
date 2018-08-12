package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateTrigger;
import org.twz.cx.abmodel.statespace.modifier.GloRateModifier;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

import java.util.Map;

public class FDShock extends PassiveModBehaviour {
    private final State S_src;
    private final Transition T_tar;
    private double Value;

    public FDShock(String name, State s_src, Transition t_tar) {
        super(name, new GloRateModifier(name, t_tar), new StateTrigger(s_src));
        S_src = s_src;
        T_tar = t_tar;
        Value = 0;
    }

    @Override
    public void impulseChange(AbsSimModel model, AbsAgent ag, double ti) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void impulseEnter(AbsSimModel model, AbsAgent ag, double ti) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void impulseExit(AbsSimModel model, AbsAgent ag, double ti) {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        shock(m, ti);
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsSimModel model, double ti) {
        obs.put(getName(), Value);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        Value = ((FDShock)be_src).Value;
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

    @Override
    public void shock(double ti, Object source, String target, Object value) {

    }

    private double evaluate(StSpABModel model) {
        double a = model.getPopulation().count("st", S_src);
        return a/model.getPopulation().count();
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
