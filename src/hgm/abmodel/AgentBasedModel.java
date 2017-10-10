package hgm.abmodel;

import hgm.abmodel.behaviour.AbsBehaviour;
import mcore.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class AgentBasedModel extends LeafModel {
    private final Population Agents;
    private Map<String, AbsBehaviour> Behaviours;

    public AgentBasedModel(String name, AbsObserver obs, mcore.Meta meta) {
        super(name, obs, meta);
        Agents = new Population();
        Behaviours = new LinkedHashMap<>();
    }


    public AbsBehaviour getBehaviours(String be) {
        return Behaviours.get(be);
    }

    public Population getPopulation() {
        return Agents;
    }

    @Override
    public void clear() {

    }

    @Override
    public void reset(double ti) {

    }

    @Override
    public void readY0(Y0<Double> y0, double ti) {

    }

    @Override
    public void listen(String src_m, String src_v, String tar_p) {

    }

    @Override
    public void listen(Collection<String> src_m, String src_v, String tar_p) {

    }

    @Override
    public void findNext() {

    }

    @Override
    public void doRequest(Request req) {

    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
