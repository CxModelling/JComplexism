package org.twz.cx.mcore;

import org.json.JSONException;
import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;
import org.twz.dag.ParameterCore;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class LeafModel extends AbsSimModel {
    public LeafModel(String name, ParameterCore pars, AbsObserver obs, IY0 protoY0) {
        super(name, pars, obs, protoY0);
    }

    public LeafModel(String name, Map<String, Double> pars, AbsObserver obs, IY0 protoY0) {
        super(name, new ParameterCore(name, null, pars, 0), obs, protoY0);
    }

    public LeafModel(String name, AbsObserver obs, IY0 protoY0) {
        super(name, ParameterCore.NullParameters, obs, protoY0);
    }

    @Override
    public List<Request> collectRequests() {
        Scheduler.findNext();
        return Scheduler.getRequests();
    }

    @Override
    public void synchroniseRequestTime(double time) {
        Scheduler.setGloTime(time);
    }

    @Override
    public void fetchRequests(List<Request> requests) {
        Scheduler.fetchRequests(requests);
    }

    @Override
    public void executeRequests() throws JSONException {
        if (Scheduler.isExecutable()) {
            for (Request request : Scheduler.getRequests()) {
                doRequest(request);
            }
            Scheduler.toExecutionCompleted();
        }
    }

    @Override
    public List<Disclosure> collectDisclosure() {
        Scheduler.reduceDisclosures(this);
        return Scheduler.popDisclosures();
    }

    @Override
    public void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) throws JSONException {
        for (Map.Entry<Disclosure, AbsSimModel> ent: ds_ms.entrySet()) {
            triggerExternalImpulses(ent.getKey(), ent.getValue(), ti);
        }
    }

}
