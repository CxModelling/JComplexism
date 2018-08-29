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

public class Cohort extends PassiveBehaviour {
    private final State S_death;
    private double DeathN;

    public Cohort(String name, State s_death) {
        super(name, new StateEnterTrigger(s_death));
        S_death = s_death;
        DeathN = 0;
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        model.kill(ag.getName(), ti);
        DeathN ++;
    }

    @Override
    public void impulseEnter(AbsAgentBasedModel model, AbsAgent ag, double ti) {

    }

    @Override
    public void impulseExit(AbsAgentBasedModel model, AbsAgent ag, double ti) {

    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName(), DeathN);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        DeathN = ((Cohort) be_src).DeathN;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {

    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    @Override
    public String toString() {
        return String.format("Reincarnation(%s, Death:%s, NDeath:%s)", getName(), S_death.getName(), DeathN);
    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("s_death", S_death.getName());
        return js;
    }
}
