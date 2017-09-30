package mcore;

import org.apache.commons.math3.analysis.function.Abs;

import java.util.List;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class LeafModel extends AbsSimModel<Double> {
    public LeafModel(String name, AbsObserver obs, Meta meta) {
        super(name, obs, meta);
    }

    @Override
    public void fetch(List<Request> rqs) {
        Requests.clear();
        Requests.add(rqs);
    }

    @Override
    public void exec() {
        Requests.getRequests().forEach(this::doRequest);
        dropNext();
    }
}
