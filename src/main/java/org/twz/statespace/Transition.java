package org.twz.statespace;


import org.json.JSONException;
import org.twz.io.AdapterJSONObject;
import org.json.JSONObject;
import org.twz.prob.IDistribution;
import org.twz.prob.ISampler;

/**
 *
 * Created by TimeWz on 2017/1/2.
 */
public class Transition implements AdapterJSONObject {
    private final String Name;
    private final State State;
    private final ISampler Dist;

    public Transition(String name, State state, ISampler dist) {
        Name = name;
        State = state;
        Dist = dist;
    }

    public double rand() {
        return Dist.sample();
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
