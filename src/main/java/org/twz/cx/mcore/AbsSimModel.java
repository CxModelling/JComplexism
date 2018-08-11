package org.twz.cx.mcore;

import org.json.JSONArray;
import org.twz.cx.element.*;
import org.twz.cx.mcore.communicator.IChecker;
import org.twz.cx.mcore.communicator.IShocker;
import org.twz.cx.mcore.communicator.ListenerSet;
import org.twz.dag.Gene;
import org.twz.io.AdapterJSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsSimModel implements AdapterJSONObject{
    private final String Name;
    protected final AbsObserver Observer;
    protected final IY0 ProtoY0;
    protected final AbsScheduler Scheduler;
    private ListenerSet Listeners;
    protected Gene Parameters;
    protected Map<String, Object> Environment;
    private double TimeEnd;


    public AbsSimModel(String name, Gene pars, AbsObserver obs, IY0 protoY0) {
        Name = name;
        Observer = obs;
        Parameters = pars;
        Environment = new HashMap<>();
        Listeners = new ListenerSet();

        Scheduler = new PriorityQueueScheduler(name);
        ProtoY0 = protoY0;
        TimeEnd = Double.NaN;
    }

    public AbsObserver getObserver() {
        return Observer;
    }

    public void initialise(double ti, JSONArray y0) {
        initialise(ti, ProtoY0.adaptTo(y0));
    }

    public void initialise(double ti, IY0 y0) {
        y0.matchModelInfo(this);
        readY0(y0, ti);
        preset(ti);
    }

    public void initialise(double ti) {
        preset(ti);
    }

    public String getName() {
        return Name;
    }

    public Object get(String s) {
        return Environment.get(s);
    }

    public double getParameter(String key) {
        return Parameters.get(key);
    }

    AbsScheduler getScheduler() {
        return Scheduler;
    }

    public Double getDouble(String s) {
        return (Double) Environment.get(s);
    }

    public String getString(String s) {
        return (String) Environment.get(s);
    }

    public void preset(double ti) {
        reset(ti);
    }

    public void reset(double ti) {
        Scheduler.rescheduleAllAtoms();
    };

    public abstract void readY0(IY0 y0, double ti);

    public abstract List<Request> collectRequests() throws Exception ;

    public abstract void doRequest(Request req);

    public void validateRequests() {
        // todo
    }

    public abstract void synchroniseRequestTime(double time);

    public abstract void fetchRequests(List<Request> requests);

    public abstract void executeRequests();

    public void disclose(String msg, String who, Map<String, Object> args) {
        Scheduler.appendDisclosure(msg, who, args);
    }

    public void disclose(String msg, String who) {
        Scheduler.appendDisclosure(msg, who);
    }

    public abstract List<Disclosure> collectDisclosure();

    public abstract void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti);

    public double getTimeEnd() {
        return TimeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        TimeEnd = timeEnd;
    }

    public void addListener(IChecker impulse, IShocker response) {
        Listeners.defineImpulseResponse(impulse, response);
    }

    public boolean triggerExternalImpulses(Disclosure dis, AbsSimModel model, double ti) {
        return Listeners.applyShock(dis, model, this, ti);
    }

    public abstract void shock(double time, Object action, String target, Object value);

    public Set<IChecker> getAllImpulseCheckers() {
        return Listeners.getAllCheckers();
    }

    public void exitCycle() {
        Scheduler.toCycleCompleted();
    }

    public void clearOutput() {
        Observer.renew();
    }

    public void setObservationalInteval(double dt) {
        Observer.setObservationalInteval(dt);
    }

    public void initialiseObservations(double ti) {
        Observer.initialiseObservations(this, ti);
    }

    public void updateObservations(double ti) {
        Observer.observeRoutinely(this, ti);
    }

    public void captureMidTermObservations(double ti) {
        Observer.updateAtMidTerm(this, ti);
    }

    public void pushObservations(double ti) {
        Observer.pushObservation(ti);
    }

    public Map<String, Double> getLastObservations() {
        return Observer.getLast();
    }

    public List<Map<String, Double>> output() {
        return Observer.getTimeSeries();
    }

    public abstract Double getSnapshot(String key, double ti);

    public void print() {
        Observer.print();
    }
}
