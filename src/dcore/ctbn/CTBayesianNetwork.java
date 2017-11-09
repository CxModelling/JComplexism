package dcore.ctbn;

import dcore.AbsDCore;
import dcore.State;
import dcore.Transition;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/8.
 */
public class CTBayesianNetwork extends AbsDCore {
    private final Map<String, State> States;
    private final Map<String, State> WellDefinedStates;
    private final Map<String, Transition> Transitions;
    private final Map<State, List<State>> Subsets;
    private final Map<State, List<Transition>> Targets;
    private final Map<State, Map<State, State>> Links;


    public CTBayesianNetwork(String name,
                             Map<String, State> states, Map<String, Transition> transitions,
                             Map<String, State> wellDefinedStates, Map<State, List<State>> subsets,
                             Map<State, List<Transition>> targets, Map<State, Map<State, State>> links, JSONObject js) {
        super(name, js);
        States = states;
        Transitions = transitions;
        WellDefinedStates = wellDefinedStates;
        Subsets = subsets;
        Targets = targets;
        Links = links;
    }


    @Override
    public Map<String, State> getStateSpace() {
        return States;
    }

    @Override
    public Map<String, State> getWellDefinedStateSpace() {
        return WellDefinedStates;
    }

    @Override
    public Map<String, Transition> getTransitionSpace() {
        return Transitions;
    }

    @Override
    public boolean isa(State s0, State s1) {
        return Subsets.get(s0).contains(s1);
    }

    @Override
    public List<Transition> getTransitions(State fr) {
        return Targets.get(fr);
    }

    @Override
    public State exec(State st, Transition tr) {
        return Links.get(st).get(tr.getState());
    }

}
