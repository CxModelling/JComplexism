package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.PassiveBehaviour;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateEnterTrigger;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.Map;

public class CumAttribute extends PassiveBehaviour {
    private String Key;
    private double Sum, SumSq, Count;

    public CumAttribute(String name, State s_src, String key) {
        super(name, new StateEnterTrigger(s_src));
        Key = key;
        Sum = 0;
        SumSq = 0;
        Count = 0;
    }


    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        impulseEnter(model, ag, ti);
    }

    @Override
    public void impulseEnter(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        Object x = ag.get(Key);
        double v;
        if (x instanceof Number) {
            v = ((Number) x).doubleValue();
        } else {
            try {
                v = (double) ag.get(Key);
            } catch (ClassCastException e) {
                v = 0;
            }
        }

        Count += 1;
        Sum += v;
        SumSq += v * v;
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName()+"_N", Count);
        obs.put(getName()+"_M", Sum);
        obs.put(getName()+"_S", SumSq);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {

    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("key", Key);
        return js;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        Sum = 0;
        SumSq = 0;
        Count = 0;
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        Sum = 0;
        SumSq = 0;
        Count = 0;
    }
}
