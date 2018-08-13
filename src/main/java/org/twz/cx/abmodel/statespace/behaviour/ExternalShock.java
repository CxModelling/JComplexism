package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.modifier.GloRateModifier;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.Transition;

import java.util.Map;

public class ExternalShock extends PassiveModBehaviour {
    private final Transition T_tar;
    private double Value;

    public ExternalShock(String name, Transition t_tar) {
        super(name, new GloRateModifier(name, t_tar), Trigger.NullTrigger);
        T_tar = t_tar;
        Value = 0;
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName(), Value);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        Value = ((ExternalShock)be_src).Value;
        ags_new.values().forEach(ag->register(ag, ti));
    }

    @Override
    protected JSONObject getArgumentJSON() {
        JSONObject js = new JSONObject();
        js.put("t_tar", T_tar.getName());
        return js;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        StSpABModel m = (StSpABModel) model;
        if (ModProto.update(Value)) {
            m.getPopulation().getAgents().values().forEach(ag->ag.modify(getName(), ti));
        }
    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    @Override
    public void shock(double ti, Object source, String target, Object value) {
        Value = (Double) value;
        if (ModProto.update(Value)) {
            StSpABModel model = (StSpABModel) source;
            model.getPopulation().getAgents().values().forEach(ag->ag.modify(getName(), ti));
        }
    }

    @Override
    public String toString() {
        return String.format("ExternalShock(%s, on %s, by %s)", getName(), T_tar.getName(), Value);
    }
}
