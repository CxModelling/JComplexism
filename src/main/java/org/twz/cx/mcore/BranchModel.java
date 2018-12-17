package org.twz.cx.mcore;

import org.json.JSONException;
import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;
import org.twz.dag.ParameterCore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class BranchModel extends AbsSimModel {
    public BranchModel(String name, ParameterCore pars, AbsObserver obs, IY0 protoY0) {
        super(name, pars, obs, protoY0);
    }

    public BranchModel(String name, Map<String, Double> pars, AbsObserver obs, IY0 protoY0) {
        this(name, new ParameterCore(name, null, pars, 0), obs, protoY0);
    }

    public abstract Map<String, AbsSimModel> getModels();

    public abstract AbsSimModel getModel(String name);

    public AbsSimModel select(String sel) {
        try {
            return getModel(sel);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ModelSelector selectAll(String sel) {
        return (new ModelSelector(getModels())).select_all(sel);
    }

    public abstract void appendModel(AbsSimModel mod);


    @Override
    public void preset(double ti) {
        getModels().values().forEach(m -> m.preset(ti));
        super.preset(ti);
    }

    @Override
    public void reset(double ti) {
        getModels().values().forEach(m -> m.reset(ti));
        super.reset(ti);
    }

    @Override
    public List<Request> collectRequests() {
        Scheduler.findNext();
        for (AbsSimModel m: getModels().values()) {
            try {
                m.collectRequests();
                Scheduler.appendLowerSchedule(m.getScheduler());
            } catch (Exception ignored) {

            }
        }
        return Scheduler.getRequests();
    }

    @Override
    public void synchroniseRequestTime(double time) {
        Scheduler.setGloTime(time);
        getModels().values().forEach(m->m.synchroniseRequestTime(time));
    }

    @Override
    public void fetchRequests(List<Request> requests) {
        Scheduler.fetchRequests(requests);

        Map<String, List<Request>> lower = Scheduler.popLowerRequests();

        for(Map.Entry<String, List<Request>> ent: lower.entrySet()) {
            getModel(ent.getKey()).fetchRequests(ent.getValue());
        }
    }

    @Override
    public void executeRequests() throws JSONException {
        for (AbsSimModel absSimModel : getModels().values()) {
            absSimModel.executeRequests();
        }

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
        List<Disclosure> dss = Scheduler.popDisclosures();
        for (AbsSimModel m: getModels().values()) {
            dss.addAll(m.collectDisclosure().stream()
                    .map(d -> d.upScale(getName()))
                    .collect(Collectors.toList()));
        }
        return dss;
    }

    @Override
    public void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) throws JSONException {
        ds_ms.entrySet().stream()
                .filter(e -> e.getValue() != this)
                .forEach(e -> {
                    try {
                        triggerExternalImpulses(e.getKey(), e.getValue(), ti);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                });

        if (Scheduler.getDisclosures().isEmpty()) Scheduler.toCycleCompleted();

        for (Map.Entry<String, AbsSimModel> ent: getModels().entrySet()) {
            Map<Disclosure, AbsSimModel> ds = new LinkedHashMap<>();
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
        getModels().values().forEach(AbsSimModel::exitCycle);
        super.exitCycle();
    }

    @Override
    public void initialiseObservations(double ti) throws JSONException {
        for (AbsSimModel m : getModels().values()) {
            m.initialiseObservations(ti);
        }
        super.initialiseObservations(ti);
    }

    @Override
    public void updateObservations(double ti) throws JSONException {
        for (AbsSimModel m : getModels().values()) {
            m.updateObservations(ti);
        }
        super.updateObservations(ti);
    }

    @Override
    public void captureMidTermObservations(double ti) throws JSONException {
        for (AbsSimModel m : getModels().values()) {
            m.captureMidTermObservations(ti);
        }
        super.captureMidTermObservations(ti);
    }

    @Override
    public void pushObservations(double ti) {
        getModels().values().forEach(m->m.pushObservations(ti));
        super.pushObservations(ti);
    }

}
