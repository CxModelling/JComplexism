package org.twz.statespace.ctmc;

import org.twz.statespace.AbsDCore;
import org.twz.statespace.State;
import org.twz.statespace.Transition;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/1/2.
 */
public class CTMarkovChain extends AbsDCore {

    private Map<String, State> States;
    private Map<String, Transition> Transitions;
    private Map<State, List<Transition>> Targets;

    public CTMarkovChain(String name, Map<String, State> sts, Map<String, Transition> trs,
                         Map<State, List<Transition>> tars, JSONObject js) {
        super(name, js);
        States = sts;
        Transitions = trs;
        Targets = tars;
    }

    @Override
    public State getState(String state) {
        return States.get(state);
    }

    @Override
    public Map<String, State> getStateSpace() {
        return States;
    }

    @Override
    public Map<String, State> getWellDefinedStateSpace() {
        return States;
    }

    @Override
    public boolean isa(State s0, State s1) {
        return s0 == s1;
    }

    @Override
    public Map<String, Transition> getTransitionSpace() {
        return Transitions;
    }

    @Override
    public List<Transition> getTransitions(State fr) {
        return Targets.get(fr);
    }

    @Override
    public State exec(State st, Transition tr) {
        return tr.getState();
    }

}
