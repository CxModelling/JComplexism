package org.twz.cx.abmodel.statespace.behaviour;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.ActiveBehaviour;
import org.twz.cx.abmodel.statespace.behaviour.trigger.StateEnterTrigger;
import org.twz.cx.element.Event;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.HashMap;
import java.util.Map;

public class LifeRate extends ActiveBehaviour {
    private final State S_death, S_birth;
    private double Dt, BirthRate;
    private double BirthN;

    public LifeRate(String name, State s_death, State s_birth, Double rate, Double dt) {
        super(name, new StepTicker(name, dt), new StateEnterTrigger(s_death));
        S_death = s_death;
        S_birth = s_birth;
        assert rate > 0;
        assert dt > 0;

        BirthRate = rate;
        Dt = dt;
        BirthN = 0;
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void impulseChange(AbsAgentBasedModel model, AbsAgent ag, double ti) {
        model.kill(ag.getName(), ti);
    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName(), BirthN);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        BirthN = ((LifeRate) be_src).BirthN;
    }

    @Override
    protected Event composeEvent(double ti) {
        return new Event(getName(), ti);
    }

    @Override
    protected void doAction(AbsSimModel model, Object todo, double ti) throws JSONException {
        double n = ((AbsAgentBasedModel) model).size();
        if (n <= 0) return;
        double prob = - Math.expm1(-BirthRate * Dt);
        if (n*prob > 0.05) {
            n = (new BinomialDistribution((int)n, prob)).sample();
        } else {
            n = (new PoissonDistribution(n *prob)).sample();
        }
        n = Math.floor(n);
        BirthN += n;
        Map<String, Object> atr = new HashMap<>();
        atr.put("st", S_birth);
        ((AbsAgentBasedModel) model).birth((int)n, ti, atr);
    }

    @Override
    public String toString() {
        return String.format("LifeRate(%s, Death:%s, Birth:%s, Rate:%s)", getName(), S_death.getName(), S_birth.getName(), BirthRate);
    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("s_death", S_death.getName());
        js.put("s_birth", S_birth.getName());
        js.put("rate", BirthRate);
        js.put("dt", Dt);
        return js;
    }
}
