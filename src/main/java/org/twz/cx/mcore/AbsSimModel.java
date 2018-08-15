package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.element.*;
import org.twz.cx.mcore.communicator.IChecker;
import org.twz.cx.mcore.communicator.IShocker;
import org.twz.cx.mcore.communicator.ListenerSet;
import org.twz.dag.Gene;
import org.twz.dag.ParameterCore;
import org.twz.dag.actor.Sampler;
import org.twz.io.AdapterJSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsSimModel implements AdapterJSONObject{
    private final String Name;
    protected final AbsObserver Observer;
    private final IY0 ProtoY0;
    protected final AbsScheduler Scheduler;
    private ListenerSet Listeners;
    private ParameterCore Parameters;
    private JSONObject Environment;
    private double TimeEnd;


    public AbsSimModel(String name, ParameterCore pars, AbsObserver obs, IY0 protoY0) {
        Name = name;
        Observer = obs;
        Parameters = pars;
        Environment = new JSONObject();
        Listeners = new ListenerSet();

        Scheduler = AbsScheduler.getScheduler(name); //new PriorityQueueScheduler(name);
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

    public String getString(String s) {
        return Environment.getString(s);
    }

    public Double getDouble(String s) {
        return Environment.getDouble(s);
    }

    public double getParameter(String key) {
        return Parameters.get(key);
    }

    public Sampler getSampler(String key) {
        return Parameters.getSampler(key);
    }

    public Gene getParameters() {
        return Parameters;
    }

    AbsScheduler getScheduler() {
        return Scheduler;
    }

    public void preset(double ti) {
        Scheduler.rescheduleAllAtoms();
        disclose("initialise", "*");
    }

    public void reset(double ti) {
        Scheduler.rescheduleAllAtoms();
        disclose("reset", "*");
    }

    public abstract void readY0(IY0 y0, double ti);

    // Event finding and execution

    public abstract List<Request> collectRequests() throws Exception ;

    public abstract void doRequest(Request req);

    public void validateRequests() {
        // todo
    }

    public void addListener(IChecker impulse, IShocker response) {
        Listeners.defineImpulseResponse(impulse, response);
    }

    boolean triggerExternalImpulses(Disclosure dis, AbsSimModel model, double ti) {
        return Listeners.applyShock(dis, model, this, ti);
    }

    public abstract void shock(double time, String action, JSONObject value);

    public Set<IChecker> getAllImpulseCheckers() {
        return Listeners.getAllCheckers();
    }

    // Scheduling

    public abstract void synchroniseRequestTime(double time);

    public abstract void fetchRequests(List<Request> requests);

    public abstract void executeRequests();

    public void disclose(String msg, String who, Map<String, Object> args) {
        Scheduler.appendDisclosure(msg, who, args);
    }

    public void disclose(String msg, String who, JSONObject args) {
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

    public void exitCycle() {
        Scheduler.toCycleCompleted();
    }

    public void printCounts() {
        Scheduler.printEventCounts();
    }

    // observation

    public void clearOutput() {
        Observer.renew();
    }

    public void setObservationalInteval(double dt) {
        Observer.setObservationalInterval(dt);
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

    public Double getSnapshot(String key, double ti) {
        return Observer.getSnapshot(this, key, ti);
    }

    public void print() {
        Observer.print();
    }
}
