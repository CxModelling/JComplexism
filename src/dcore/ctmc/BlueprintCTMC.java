package dcore.ctmc;

import dcore.IBlueprintDCore;
import dcore.State;
import dcore.Transition;
import dcore.ctbn.BlueprintCTBN;
import org.json.JSONObject;
import pcore.ParameterCore;
import pcore.ScriptException;
import pcore.distribution.IDistribution;
import utils.json.FnJSON;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 25/01/2017.
 */
public class BlueprintCTMC implements IBlueprintDCore<CTMarkovChain> {

    private String Name;
    private Map<String, String> States;
    private Map<String, String> TransitionTo;
    private Map<String, String> TransitionBy;
    private Map<String, List<String>> Target;

    public BlueprintCTMC(String name) {
        Name = name;
        States = new HashMap<>();
        TransitionTo = new HashMap<>();
        TransitionBy = new HashMap<>();
        Target = new HashMap<>();
    }

    public BlueprintCTMC(JSONObject js) throws ScriptException{
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
    }

    public void addState(String name, String detail) {
        if (States.containsKey(name)) return;
        States.put(name, detail);
        Target.put(name, new ArrayList<>());
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
        Target.get(st).add(tr);
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
    public boolean isCompatible(ParameterCore pc) {
        IDistribution dist;
        for (Map.Entry<String, String> ent: TransitionBy.entrySet()) {
            try {
                dist = pc.getDistribution(ent.getValue());
                if (dist.getLower() < 0) {
                    System.out.println("Distribution "+ ent.getValue() +
                            " is not non-negative");
                    return false;
                }
            } catch (NullPointerException e) {
                System.out.println("Distribution "+ ent.getValue() +
                        " does not exist");
                return false;
            }
        }
        return true;
    }

    @Override
    public String[] getRequiredDistributions() {
        return (String[]) TransitionBy.values().toArray();
    }

    @Override
    public CTMarkovChain generateModel(ParameterCore pc) {
        Map<String, State> sts = new HashMap<>();
        Map<String, Transition> trs = new HashMap<>();
        Map<State, List<Transition>> tars = new HashMap<>();

        for (Map.Entry<String, String> ent: States.entrySet()) {
            sts.put(ent.getKey(), new State(ent.getKey()));
        }
        for (Map.Entry<String, String> ent: TransitionTo.entrySet()) {
            trs.put(ent.getKey(), new Transition(ent.getKey(),
                    sts.get(ent.getValue()), pc.getDistribution(TransitionBy.get(ent.getKey()))));
        }
        for (Map.Entry<String, List<String>> ent: Target.entrySet()) {
            tars.put(sts.get(ent.getKey()),
                    ent.getValue().stream().map(trs::get).collect(Collectors.toList()));
        }
        CTMarkovChain mod = new CTMarkovChain(Name, sts, trs, tars);
        sts.values().forEach(st -> st.setModel(mod));
        return mod;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
