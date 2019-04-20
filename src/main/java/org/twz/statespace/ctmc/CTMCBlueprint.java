package org.twz.statespace.ctmc;

import org.json.JSONException;
import org.twz.dag.Parameters;
import org.twz.dag.actor.Sampler;
import org.twz.prob.DistributionManager;
import org.twz.prob.IDistribution;
import org.twz.statespace.IStateSpaceBlueprint;
import org.twz.statespace.State;
import org.twz.statespace.Transition;
import org.json.JSONObject;
import org.twz.dag.ParameterCore;
import org.twz.io.FnJSON;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 25/01/2017.
 */
public class CTMCBlueprint implements IStateSpaceBlueprint<CTMarkovChain> {

    private String Name;
    private Map<String, String> States;
    private Map<String, String> TransitionTo;
    private Map<String, String> TransitionBy;
    private Map<String, List<String>> Targets;

    private JSONObject JS;

    public CTMCBlueprint(String name) {
        Name = name;
        States = new HashMap<>();
        TransitionTo = new HashMap<>();
        TransitionBy = new HashMap<>();
        Targets = new HashMap<>();
        JS = null;
    }

    public CTMCBlueprint(JSONObject js) throws JSONException {
        this(js.getString("ModelName"));
        JSONObject sub, temp;
        Iterator<?> keys;

        FnJSON.toStringList(js.getJSONArray("States")).forEach(this::addState);

        sub = js.getJSONObject("Transitions");
        keys = sub.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            temp = sub.getJSONObject(key);
            addTransition(key, temp.getString("To"), temp.getString("Dist"));
        }

        sub = js.getJSONObject("Targets");
        keys = sub.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            linkStateTransitions(key, FnJSON.toStringArray(sub.getJSONArray(key)));
        }
        JS = js;
    }

    public void addState(String name, String detail) {
        if (States.containsKey(name)) return;
        States.put(name, detail);
        Targets.put(name, new ArrayList<>());
    }

    public void addState(String name) {
        addState(name, name);
    }

    public void addTransition(String name, String to) {
        addTransition(name, to, name);
    }

    public void addTransition(String name, String to, String by) {
        addState(to);
        if (TransitionTo.containsKey(name)) return;
        TransitionTo.put(name, to);
        TransitionBy.put(name, by);
    }

    public boolean linkStateTransition(String st, String tr) {
        addState(st);
        if (!TransitionTo.containsKey(tr)) return false;
        Targets.get(st).add(tr);
        return true;
    }

    public boolean linkStateTransitions(String state, String[] trs) {
        boolean all = true;
        for (String tr: trs) {
            all &= linkStateTransition(state, tr);
        }
        return all;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public boolean isCompatible(Parameters pc) {
        for (Map.Entry<String, String> ent: TransitionBy.entrySet()) {
            try {
                pc.getSampler(ent.getValue());
            } catch (NullPointerException e) {
                System.out.println("Sampler "+ ent.getValue() + " does not exist");
                return false;
            }
        }
        return true;
    }

    @Override
    public String[] getRequiredDistributions() {
        String[] res = new String[TransitionBy.size()];
        List<String> list = new ArrayList<>(TransitionBy.values());

        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }

        return res;
    }

    @Override
    public CTMarkovChain generateModel(Parameters pc) {
        Map<String, State> sts = new HashMap<>();
        Map<String, Transition> trs = new HashMap<>();
        Map<State, List<Transition>> tars = new HashMap<>();


        for (Map.Entry<String, String> ent: States.entrySet()) {
            sts.put(ent.getKey(), new State(ent.getKey()));
        }

        String di;

        for (Map.Entry<String, String> ent: TransitionTo.entrySet()) {
            // todo to optimise
            di = TransitionBy.get(ent.getKey());
            if (di.contains("(")) {
                trs.put(ent.getKey(), new Transition(ent.getKey(), sts.get(ent.getValue()),
                        DistributionManager.parseDistribution(di)));
            } else {
                trs.put(ent.getKey(), new Transition(ent.getKey(), sts.get(ent.getValue()), di));
            }
        }
        for (Map.Entry<String, List<String>> ent: Targets.entrySet()) {
            tars.put(sts.get(ent.getKey()),
                    ent.getValue().stream().map(trs::get).collect(Collectors.toList()));
        }

        JSONObject js;
        try {
            js = toJSON();
        } catch (JSONException e) {
            js = null;
        }
        CTMarkovChain mod = new CTMarkovChain(Name, sts, trs, tars, js);
        sts.values().forEach(st -> st.setModel(mod));
        return mod;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        if (JS == null) {
            buildJSON();
        }
        return JS;
    }

    public void buildJSON() throws JSONException {
        JS  = new JSONObject();

        JS.put("ModelType", "CTMC");
        JS.put("ModelName", Name);
        JS.put("States", States);

        JSONObject trs = new JSONObject();

        for (Map.Entry<String, String> entry : TransitionTo.entrySet()) {
            trs.put(entry.getKey(), new JSONObject("{'Dist': "+TransitionBy.get(entry.getKey())+", 'To': "+entry.getValue()+"}"));
        }

        JS.put("Transitions", trs);

        JS.put("Targets", Targets);
    }
}
