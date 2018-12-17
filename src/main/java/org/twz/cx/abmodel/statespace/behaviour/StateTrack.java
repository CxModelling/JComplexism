package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.PassiveBehaviour;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpAgent;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateTrigger;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.ArrayList;
import java.util.List;
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
    public void initialise(double ti, AbsSimModel model) throws JSONException {
        StSpABModel m = (StSpABModel) model;
        Value = evaluate(m);
        JSONObject js = new JSONObject();
        try {
            js.put("v1", Value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        model.disclose("update value to "+ Value, getName(), js);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public List<Disclosure> manageDisclosures(List<Disclosure> dis) {
        if (dis.size() == 1) {
            return dis;
        }

        Disclosure d0 = dis.get(0), d1 = dis.get(dis.size()-1);

        JSONObject js = new JSONObject();
        try {
            js.put("v0", d0.getDouble("v0"));
            js.put("v1", Value);
        } catch (JSONException ignored) {

        }

        List<Disclosure> res = new ArrayList<>();
        res.add(new Disclosure("update value to "+ Value,
                getName(), d1.Where, js));
        return res;
    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) throws JSONException {
        if (((StSpAgent) ag).isa(S_src)) {
            changeValue(model, 1);
        } else {
            changeValue(model, -1);
        }
    }

    @Override
    public void impulseEnter(AbsAgentBasedModel model, AbsAgent ag, double ti) throws JSONException {
        changeValue(model, 1);
    }

    @Override
    public void impulseExit(AbsAgentBasedModel model, AbsAgent ag, double ti) throws JSONException {
        changeValue(model, -1);
    }

    private void changeValue(AbsAgentBasedModel model, double dv) throws JSONException {
        JSONObject js = new JSONObject();
        double v0 = Value, v1 = Value + dv;
        js.put("v0", v0);
        js.put("v1", v1);
        Value = v1;
        model.disclose("update value to "+ Value, getName(), js);
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
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("s_src", S_src.getName());
        return js;
    }

    private double evaluate(StSpABModel model) throws JSONException {
        return model.getPopulation().count("st", S_src);
    }

    @Override
    public String toString() {
        return String.format("StateTrack(%s, %s)", getName(), S_src.getName());
    }
}
