package dcore;


import hgm.utils.AdapterJSONObject;
import org.json.JSONObject;
import pcore.distribution.IDistribution;

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
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("To", State.getName());
        js.put("Dist", Dist.getName());
        return js;
    }
}
