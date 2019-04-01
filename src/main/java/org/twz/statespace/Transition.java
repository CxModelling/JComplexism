package org.twz.statespace;


import org.json.JSONException;
import org.twz.exception.IncompleteConditionException;
import org.twz.io.AdapterJSONObject;
import org.json.JSONObject;
import org.twz.prob.IDistribution;

/**
 *
 * Created by TimeWz on 2017/1/2.
 */
public class Transition implements AdapterJSONObject {
    private final String Name;
    private final State State;
    private final IDistribution Dist;

    public Transition(String name, State state, IDistribution dist) {
        Name = name;
        State = state;
        Dist = dist;
    }

    public double rand() throws IncompleteConditionException {
        return Dist.sample(); // todo
    }

    public State getState() {
        return State;
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return Name + "(" + State + ", " + Dist.toString() + ")";
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("To", State.getName());
        js.put("Dist", Dist.getName());
        return js;
    }
}
