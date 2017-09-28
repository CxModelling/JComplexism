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
public abstract class BranchModel extends AbsSimModel {
    private Map<String, AbsSimModel> Models;

    public BranchModel(String name, AbsObserver obs, Meta meta) {
        super(name, obs, meta);
        Models = new HashMap<>();
    }

    @Override
    public void fetch(List<Request> rqs) {
        Requests.clear();
        Requests.add(rqs);
        passDown();
    }

    @Override
    public void exec() {
        Requests.getRequests().forEach(this::doRequest);
        Models.values().stream()
                .filter(v -> v.tte() == tte())
                .forEach(AbsSimModel::exec);
        Requests.clear();
    }

    public void passDown() {
        List<Request> rqs = Requests.popLowerRequests();
        Map<String, List<Request>> nest = new HashMap<>();
        Pair<String, Request> pr;
        for (Request req: rqs) {
            pr = req.down();
            if (nest.containsKey(pr.getKey())) {
                nest.put(pr.getKey(), new ArrayList<>());
            }
            nest.get(pr.getKey()).add(pr.getValue());
        }
        nest.forEach((key, value) -> Models.get(key).fetch(value));
    }
}
