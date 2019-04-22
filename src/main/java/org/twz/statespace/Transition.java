package org.twz.statespace;


import org.json.JSONException;
import org.twz.dag.Parameters;
import org.twz.exception.IncompleteConditionException;
import org.twz.io.AdapterJSONObject;
import org.json.JSONObject;
import org.twz.prob.IDistribution;

import java.util.Collection;

/**
 *
 * Created by TimeWz on 2017/1/2.
 */
public class Transition implements AdapterJSONObject {
    private final String Name;
    private final State State;
    private final IDistribution Dist;
    private final String DistName;

    public Transition(String name, State state, IDistribution dist) {
        Name = name;
        State = state;
        Dist = dist;
        DistName = Dist.getName();
    }

    public Transition(String name, State state, String dist) {
        Name = name;
        State = state;
        Dist = null;
        DistName = dist;
    }

    public double rand() {
        return Dist.sample();
    }

    public double rand(Parameters pars) {
        return pars.getSampler(DistName).sample();
    }

    public boolean affected(Collection<String> pars) {
        if (Dist != null) return false;
        return pars.contains(DistName);
    }

    public boolean affected(String par) {
        return DistName.equals(par);
    }

    public State getState() {
        return State;
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return Name + "(" + State + ", " + DistName + ")";
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("To", State.getName());
        js.put("Dist", DistName);
        return js;
    }
}
