package mcore;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class BranchModel extends AbsSimModel<Y0> {
    protected Map<String, AbsSimModel> Models;

    public BranchModel(String name, AbsObserver obs, Meta meta) {
        super(name, obs, meta);
        Models = new HashMap<>();
    }

    @Override
    public void readY0(Y0<Y0> y0s, double ti) {
        for (Map.Entry<String, Y0> y0: y0s.entrySet()) {
            Models.get(y0.getKey()).readY0(y0.getValue(), ti);
        }
    }

    @Override
    public void fetch(List<Request> rqs) {
        Requests.clear();
        Requests.add(rqs);
        passDown();
    }

    @Override
    public void exec() {
        Models.values().stream()
                .filter(v -> v.tte() == tte())
                .forEach(AbsSimModel::exec);
        Requests.getRequests().forEach(this::doRequest);
        dropNext();
    }

    public void passDown() {
        List<Request> rqs = Requests.popLowerRequests();
        Map<String, List<Request>> nest = new HashMap<>();
        Pair<String, Request> pr;
        Request rq;
        for (Request req: rqs) {
            pr = req.down();
            rq = pr.getSecond();
            if (!nest.containsKey(rq.getAddress())) {
                nest.put(rq.getAddress(), new ArrayList<>());
            }
            nest.get(rq.getAddress()).add(pr.getValue());
        }
        //System.out.println(nest);
        //System.out.println(Models.keySet());
        nest.forEach((key, value) -> Models.get(key).fetch(value));
    }

    public AbsSimModel select(String sel) {
        try {
            return Models.get(sel);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Map<String, AbsSimModel> getModels() {
        return Models;
    }

    public AbsSimModel getModel(String m) {
        return Models.get(m);
    }

    public ModelSelector selectAll(String sel) {
        return (new ModelSelector(Models)).select_all(sel);
    }

    public void append(AbsSimModel mod) {
        Models.put(mod.getName(), mod);
    }
}
