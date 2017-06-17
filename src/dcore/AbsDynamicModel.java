package dcore;


import org.json.JSONObject;
import java.util.List;
import java.util.Map;


/**
 *
 * Created by TimeWz on 2017/1/25.
 */
public abstract class AbsDynamicModel {
    private final String Name;

    public AbsDynamicModel(String name) {
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

    public Transition getTransition(String tr) {
        return getTransitionSpace().get(tr);
    }

    public abstract Map<String, Transition> getTransitionSpace();

    public abstract boolean isa(State s0, State s1);

    public abstract List<Transition> getTransitions(State fr);

    public abstract State exec(State st, Transition tr);

    public abstract JSONObject toJSON();
}
