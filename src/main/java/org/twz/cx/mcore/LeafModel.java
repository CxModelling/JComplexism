package org.twz.cx.mcore;

import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class LeafModel extends AbsSimModel {
    public LeafModel(String name, Map<String, Object> env, AbsObserver<AbsSimModel> obs, IY0 protoY0) {
        super(name, env, obs, protoY0);
    }

    @Override
    public List<Request> collectRequests() throws Exception {
        if (Scheduler.isWaitingForCollection()) {
            findNext();
            Scheduler.collectionCompleted();
            return Scheduler.getRequests();
        } else if (Scheduler.isWaitingForValidation()) {
            return Scheduler.getRequests();
        } else {
            throw new Exception("");
        }
    }

    @Override
    public void fetchRequests(List<Request> requests) {
        Scheduler.fetchRequest(requests);
    }

    @Override
    public void executeRequests() {
        if (Scheduler.isWaitingForExecution()) {
            for (Request req: Scheduler.getRequests()) {
                doRequest(req);
            }
            Scheduler.executionCompleted();
        }
    }

    @Override
    public List<Disclosure> collectDisclosure() {
        return Scheduler.popDisclosures();
    }

    @Override
    public void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) {
        for (Map.Entry<Disclosure, AbsSimModel> ent: ds_ms.entrySet()) {
            triggerExternalImpulses(ent.getKey(), ent.getValue(), ti);
        }
    }

}
