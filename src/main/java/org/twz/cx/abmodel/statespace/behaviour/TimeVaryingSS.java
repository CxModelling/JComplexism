package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.ActiveBehaviour;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.element.Event;

import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.AbsSimModel;

import java.util.Map;

public class TimeVaryingSS extends ActiveBehaviour {

    private String TimeKey;
    private double Dt;

    public TimeVaryingSS(String name, String key, Double dt) {
        super(name, new StepTicker(name, dt), Trigger.NullTrigger);
        TimeKey = key;
        Dt = dt;
    }

    @Override
    protected Event composeEvent(double ti) {
        return new Event(getName(), ti);
    }

    @Override
    protected void doAction(AbsSimModel model, Object todo, double ti) {
        StSpABModel m = (StSpABModel) model;

        m.getParameters().impulse(TimeKey, ti);
        m.shockParameter(TimeKey, ti);
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {

    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        TimeVaryingSS src = (TimeVaryingSS) be_src;
        TimeKey = src.TimeKey;
        Dt = src.Dt;
    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("key", TimeKey);
        js.put("dt", Dt);
        return js;
    }

    @Override
    public String toString() {
        return String.format("TimeVaryingSS(%s, TimeKey:%s)", getName(), TimeKey);
    }

}
