package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.ActiveBehaviour;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpAgent;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateTrigger;
import org.twz.cx.element.Event;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;

public class StateTrackApprox extends ActiveBehaviour {
    private final State S_src;
    private double Dt, V0, Vi, T0, Ti, CumV;


    public StateTrackApprox(String name, State s_src, Double dt) {
        super(name, new StepTicker(name, dt), new StateTrigger(s_src));
        S_src = s_src;
        Dt = dt;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        StSpABModel m = (StSpABModel) model;
        V0 = Vi = evaluate(m);
        T0 = Ti = ti;

        JSONObject js = new JSONObject();
        try {
            js.put("v1", Vi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        model.disclose("update value to "+ Vi, getName(), js);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        StSpABModel m = (StSpABModel) model;
        V0 = Vi = evaluate(m);
        T0 = Ti = ti;
    }

    @Override
    protected Event composeEvent(double ti) {
        return new Event(getName(), ti);
    }

    @Override
    protected void doAction(AbsSimModel model, Object todo, double ti) {
        double v0 = V0;

        if (ti > Ti) {
            CumV += (ti-Ti)*Vi;
            Ti = ti;
            V0 = CumV/(Ti - T0);
            CumV = 0;
            T0 = ti;
        } else {
            V0 = Vi;
        }
        if (Double.isNaN(V0)) V0 = 0;
        assert Vi == evaluate((StSpABModel) model);
        JSONObject js = new JSONObject();
        try {
            js.put("v0", v0);
            js.put("v1", V0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        model.disclose("update value to "+ V0, getName(), js);
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        if (((StSpAgent) ag).isa(S_src)) {
            changeValue( 1, ti);
        } else {
            changeValue( -1, ti);
        }
    }

    @Override
    public void impulseEnter(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        changeValue( 1, ti);
    }

    @Override
    public void impulseExit(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        changeValue(-1, ti);
    }

    private void changeValue(double dv, double ti) {
        if (ti > Ti) {
            CumV += (ti - Ti) * Vi;
            Ti = ti;
        }
        Vi += dv;
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        if (ti > T0) {
            obs.put(getName(), (CumV + (ti-Ti)*Vi)/(ti - T0));
        } else {
            obs.put(getName(), V0);
        }
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        V0 = ((StateTrackApprox)be_src).V0;
        Vi = ((StateTrackApprox)be_src).Vi;
    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("s_src", S_src.getName());
        js.put("dt", Dt);
        return js;
    }

    private double evaluate(StSpABModel model) {
        return model.getPopulation().count("State", S_src);
    }

    @Override
    public String toString() {
        return String.format("StateTrackApprox(%s, %s, %f)", getName(), S_src.getName(), Dt);
    }
}
