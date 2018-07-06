package org.twz.cx.mcore;

import org.json.JSONArray;
import org.twz.cx.element.*;
import org.twz.io.AdapterJSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsSimModel implements AdapterJSONObject{
    private final String Name;
    protected final AbsObserver<AbsSimModel> Obs;
    protected final IY0 ProtoY0;
    protected final Schedule Scheduler;
    private List<IEventListener> Listeners;
    protected Map<String, Object> Environment;
    private double TimeEnd;


    public AbsSimModel(String name, Map<String, Object> env, AbsObserver<AbsSimModel> obs, IY0 protoY0) {
        Name = name;
        Scheduler = new Schedule(name);
        Obs = obs;
        Environment = env;
        Listeners = new ArrayList<>();
        ProtoY0 = protoY0;
        TimeEnd = Double.NaN;
    }

    public AbsObserver getObserver() {
        return Obs;
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

    Schedule getScheduler() {
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

    public abstract void reset(double ti);

    public abstract void readY0(IY0 y0, double ti);

    public abstract List<Request> collectRequests() throws Exception ;

    public abstract void findNext();

    public void request(Event evt, String who) {
        Scheduler.appendRequestFromSource(evt, who);
    }

    public abstract void doRequest(Request req);

    public abstract void validateRequests();

    public abstract void fetchRequests(List<Request> requests);

    public abstract void executeRequests();

    public void disclose(String msg, String who, Map<String, Object> args) {
        Scheduler.appendDisclosureFromSource(msg, who, args);
    }

    public void disclose(String msg, String who) {
        Scheduler.appendDisclosureFromSource(msg, who);
    }

    public abstract List<Disclosure> collectDisclosure();

    public abstract void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti);

    public double getTimeEnd() {
        return TimeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        TimeEnd = timeEnd;
    }

    public abstract void addListener(IEventListener listener);

    public <E extends IY0> boolean triggerExternalImpulses(Disclosure dis, AbsSimModel model, double ti) {
        boolean shocked = false;

        for (IEventListener el: Listeners) {
            boolean needs = el.needs(dis, this);
            if (needs) {
                el.applyShock(dis, model, this, ti);
                shocked = true;
            }
        }
        return shocked;
    }

    public void exitCycle() {
        if (! Scheduler.isWaitingForValidation()) {
            Scheduler.collectionCompleted();
        }
    }

    public void clearOutput() {
        Obs.renew();
    }

    public void initialiseObservations(double ti) {
        Obs.initialiseObservations(this, ti);
    }

    public void updateObservations(double ti) {
        Obs.observeRoutinely(this, ti);
    }

    public void captureMidTermObservations(double ti) {
        Obs.updateAtMidTerm(this, ti);
    }

    public void pushObservations(double ti) {
        Obs.pushObservation(ti);
    }

    public Map<String, Double> getLastObservations() {
        return Obs.getLast();
    }

    public List<Map<String, Double>> output() {
        return Obs.getTimeSeries();
    }

    public abstract Double getSnapshot(String key, double ti);

    public void print() {
        Obs.print();
    }
}
