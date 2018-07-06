package org.twz.cx.mcore;

import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class BranchModel extends AbsSimModel {
    protected Map<String, AbsSimModel> Models;

    public BranchModel(String name, Map<String, Object> env, AbsObserver<AbsSimModel> obs, IY0 protoY0) {
        super(name, env, obs, protoY0);
        Models = new HashMap<>();
    }

    @Override
    public void preset(double ti) {
        Models.values().forEach(m -> m.preset(ti));
    }

    @Override
    public void reset(double ti) {
        Models.values().forEach(m -> m.reset(ti));
    }

    @Override
    public List<Request> collectRequests() {
        if (Scheduler.isWaitingForCollection()) {
            findNext();
            for (AbsSimModel m: Models.values()) {
                try {
                    m.collectRequests();
                    Scheduler.appendLowerSchedule(m.getScheduler());
                } catch (Exception ignored) {

                }
            }
            Scheduler.collectionCompleted();
        }
        return Scheduler.getRequests();
    }

    @Override
    public void validateRequests() {
        // todo;
    }

    @Override
    public void fetchRequests(List<Request> requests) {
        Scheduler.fetchRequest(requests);

        Map<String, List<Request>> lower = Scheduler.popLowerRequests();

        for(Map.Entry<String, List<Request>> ent: lower.entrySet()) {
            Models.get(ent.getKey()).fetchRequests(ent.getValue());
        }
        if (lower.size() > 0 | Scheduler.getRequests().size() > 0) {
            Scheduler.validationCompleted();
        }
    }

    @Override
    public void executeRequests() {
        Models.values().forEach(AbsSimModel::executeRequests);

        if (Scheduler.isWaitingForExecution()) {
            Scheduler.getRequests().forEach(this::doRequest);
            Scheduler.executionCompleted();
        }
    }

    @Override
    public List<Disclosure> collectDisclosure() {
        List<Disclosure> dss = Scheduler.popDisclosures();
        for (AbsSimModel m: Models.values()) {
            dss.addAll(m.collectDisclosure().stream()
                    .map(d -> d.upScale(getName()))
                    .collect(Collectors.toList()));
        }
        return dss;
    }

    @Override
    public void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) {
        ds_ms.entrySet().stream()
                .filter(e -> e.getValue() != this)
                .forEach(e -> triggerExternalImpulses(e.getKey(), e.getValue(), ti));

        if (Scheduler.getDisclosures().isEmpty()) Scheduler.cycleCompleted();

        for (Map.Entry<String, AbsSimModel> ent: Models.entrySet()) {
            Map<Disclosure, AbsSimModel> ds = new HashMap<>();
            for (Map.Entry<Disclosure, AbsSimModel> dm: ds_ms.entrySet()) {
                if (dm.getKey().getGroup().equals(ent.getKey())) {
                    if (!dm.getKey().getSource().equals(ent.getKey())) {
                        ds.put(dm.getKey().downScale().getSecond(), dm.getValue());
                    }
                } else {
                    ds.put(dm.getKey().siblingScale(), dm.getValue());
                }
            }
            if (!ds.isEmpty()) {
                ent.getValue().fetchDisclosures(ds, ti);
            }
        }
    }

    @Override
    public void exitCycle() {
        Models.values().forEach(AbsSimModel::exitCycle);
        super.exitCycle();
    }

    @Override
    public void initialiseObservations(double ti) {
        Models.values().forEach(m->m.initialiseObservations(ti));
        super.initialiseObservations(ti);
    }

    @Override
    public void updateObservations(double ti) {
        Models.values().forEach(m->m.updateObservations(ti));
        super.updateObservations(ti);
    }

    @Override
    public void captureMidTermObservations(double ti) {
        Models.values().forEach(m->m.captureMidTermObservations(ti));
        super.captureMidTermObservations(ti);
    }

    @Override
    public void pushObservations(double ti) {
        Models.values().forEach(m->m.pushObservations(ti));
        super.pushObservations(ti);
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
