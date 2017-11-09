package dcore;


import utils.json.AdapterJSONObject;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * Created by TimeWz on 2017/1/25.
 */
public abstract class AbsDCore implements AdapterJSONObject {
    private final String Name;
    private String JS;

    public AbsDCore(String name, JSONObject js) {
        Name = name;
        JS = js.toString();
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
        LinkedList<State> wait = st.stream()
                .filter(s -> getWellDefinedStateSpace().containsKey(s))
                .map(this::getState).collect(Collectors.toCollection(LinkedList::new));

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

    public JSONObject toJSON() {
        return new JSONObject(JS);
    }
}
