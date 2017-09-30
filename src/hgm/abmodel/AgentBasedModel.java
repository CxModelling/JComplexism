package hgm.abmodel;

import mcore.*;
import org.json.JSONObject;

import java.util.Collection;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class AgentBasedModel extends LeafModel {
    public AgentBasedModel(String name, AbsObserver obs, mcore.Meta meta) {
        super(name, obs, meta);
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
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void doRequest(Request req) {

    }

    @Override
    public String toJSONString() {
        return null;
    }
}
