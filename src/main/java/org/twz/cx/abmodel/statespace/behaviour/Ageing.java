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
import org.twz.cx.element.Ticker.AbsTicker;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.AbsSimModel;

import java.util.Map;

public class Ageing extends ActiveBehaviour {
    private String AgeKey;

    public Ageing(String name, String key) {
        super(name, new StepTicker(name, 1.0), Trigger.NullTrigger);
        AgeKey = key;
    }

    @Override
    protected Event composeEvent(double ti) {
        return new Event(getName(), ti);
    }

    @Override
    protected void doAction(AbsSimModel model, Object todo, double ti) {
        StSpABModel m = (StSpABModel) model;

        double age;
        for (AbsAgent ag : ((StSpABModel)model).getPopulation().getAgents().values()) {
            age = ag.getParameter(AgeKey);
            ag.getParameters().impulse(AgeKey, age + 1);
        }
        m.shockParameter(AgeKey, ti);
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {

    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        AgeKey = ((Ageing) be_src).AgeKey;
    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("key", AgeKey);
        return js;
    }

    @Override
    public String toString() {
        return String.format("Ageing(%s, AgeKey:%s)", getName(), AgeKey);
    }
}
