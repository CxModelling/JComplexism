package dcore;


import hgm.utils.AdapterJSONObject;
import org.json.JSONObject;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/1/25.
 */
public abstract class AbsDCore implements AdapterJSONObject {
    private final String Name;

    public AbsDCore(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public String toString() {
        return toJSON().toString();
    }

    public State getState(String st) {
        return getStateSpace().get(st);
    }

    public abstract Map<String, State> getStateSpace();

    public abstract Map<String, State> getWellDefinedStateSpace();

    public Set<State> getAccessibleStates(List<String> st) {
        HashSet<State> acc = new HashSet<>(), ed = new HashSet<>();
        LinkedList<State> wait = new LinkedList<>();
        for (String s : st) {
            if (getWellDefinedStateSpace().containsKey(s)) {
                wait.add(getState(s));
            }
        }
        State s1, s;
        while (!wait.isEmpty()) {
            s = wait.pop();
            acc.add(s);
            ed.add(s);

            for (Transition tr: s.getNextTransitions()) {
                s1 = s.exec(tr);
                if (!ed.contains(s1)) {
                    wait.add(s1);
                }
            }
        }
        return acc;
    }

    public Transition getTransition(String tr) {
        return getTransitionSpace().get(tr);
    }

    public abstract Map<String, Transition> getTransitionSpace();

    public abstract boolean isa(State s0, State s1);

    public abstract List<Transition> getTransitions(State fr);

    public abstract State exec(State st, Transition tr);

    public abstract JSONObject toJSON();
}
