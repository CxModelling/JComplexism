package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.AbsAgentBasedModel;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.behaviour.PassiveBehaviour;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.statespace.State;

import java.util.HashMap;
import java.util.Map;

public class AgentImport extends PassiveBehaviour {
    private final State S_birth;
    private double BirthN;

    public AgentImport(String name, State s_birth) {
        super(name);
        S_birth = s_birth;
        BirthN = 0;
    }

    @Override
    public void register(AbsAgent ag, double ti) {

    }

    @Override
    public void fillData(Map<String, Double> obs, AbsAgentBasedModel model, double ti) {
        obs.put(getName(), BirthN);
    }

    @Override
    public void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti) {
        BirthN = ((AgentImport) be_src).BirthN;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {

    }

    @Override
    public void reset(double ti, AbsSimModel model) {

    }

    @Override
    public void shock(double ti, AbsSimModel source, String target, JSONObject value) throws JSONException {
        int v = value.getInt("n");
        if (v > 0) {
            AbsAgentBasedModel model = (AbsAgentBasedModel) source;
            Map<String, Object> atr = new HashMap<>();
            atr.put("st", S_birth);
            model.birth(v, ti, atr);
            BirthN += v;
        }

    }

    @Override
    public String toString() {
        return String.format("AgentImport(%s, Birth:%s, NBir:%s)", getName(), S_birth.getName(), BirthN);
    }

    @Override
    protected JSONObject getArgumentJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("s_birth", S_birth.getName());
        return js;
    }
}
