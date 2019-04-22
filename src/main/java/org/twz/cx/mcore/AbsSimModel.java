package org.twz.cx.mcore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.*;
import org.twz.cx.mcore.communicator.IChecker;
import org.twz.cx.mcore.communicator.IResponse;
import org.twz.cx.mcore.communicator.ListenerSet;
import org.twz.dag.Parameters;
import org.twz.dag.actor.Sampler;
import org.twz.dataframe.DataFrame;
import org.twz.dataframe.TimeSeries;
import org.twz.exception.IncompleteConditionException;
import org.twz.io.AdapterJSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsSimModel implements AdapterJSONObject {
    private final String Name;
    protected final AbsObserver Observer;
    private final IY0 ProtoY0;
    protected final AbsScheduler Scheduler;
    private ListenerSet Listeners;
    private Parameters Pars;
    private Map<String, Object> Environment;
    private double TimeEnd;


    public AbsSimModel(String name, Parameters pars, AbsObserver obs, IY0 protoY0) {
        Name = name;
        Observer = obs;
        Pars = pars;
        Environment = new HashMap<>();
        Listeners = new ListenerSet();

        Scheduler = AbsScheduler.getScheduler(name); //new PriorityQueueScheduler(name);
        ProtoY0 = protoY0;
        TimeEnd = Double.NaN;
    }

    public AbsObserver getObserver() {
        return Observer;
    }

    public void initialise(double ti, JSONArray y0) throws JSONException {
        initialise(ti, ProtoY0.adaptTo(y0));
    }

    public void initialise(double ti, IY0 y0) throws JSONException {
        y0.matchModelInfo(this);
        try {
            readY0(y0, ti);
        } catch (JSONException e) {
            throw new ClassFormatError("Unknown format of Y0");
        }
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
        return Environment.get(s).toString();
    }

    public Double getDouble(String s) {
        return (double) get(s);
    }

    public double getParameter(String key) {
        return Pars.getDouble(key);
    }

    public Sampler getSampler(String key) {
        return Pars.getSampler(key);
    }

    public Parameters getParameters() {
        return Pars;
    }

    public abstract ModelAtom getAtom(String atom);

    public IY0 getProtoY0() {
        return ProtoY0;
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

    public abstract void readY0(IY0 y0, double ti) throws JSONException;

    // Event finding and execution

    public abstract List<Request> collectRequests() throws Exception ;

    public abstract void doRequest(Request req) throws JSONException;

    public void addListener(IChecker impulse, IResponse response) {
        Listeners.defineImpulseResponse(impulse, response);
    }

    boolean triggerExternalImpulses(Disclosure dis, AbsSimModel model, double ti) throws JSONException, IncompleteConditionException {
        return Listeners.applyShock(dis, model, this, ti);
    }

    public abstract void shock(double time, String action, JSONObject value) throws JSONException;

    public Set<IChecker> getAllImpulseCheckers() {
        return Listeners.getAllCheckers();
    }

    // Scheduling

    public abstract void synchroniseRequestTime(double time);

    public abstract void fetchRequests(List<Request> requests);

    public abstract void executeRequests() throws JSONException;

    public void disclose(String msg, String who, Map<String, Object> args) {
        Scheduler.appendDisclosure(msg, who, args);
    }

    public void disclose(String msg, String who, JSONObject args) {
        Scheduler.appendDisclosure(msg, who, args);
    }

    public void disclose(String msg, String who) {
        Scheduler.appendDisclosure(msg, who);
    }

    public List<Disclosure> manageDisclosures(String atom, List<Disclosure> dis) {
        if (atom.equals("*")) {
            return manageDisclosures(dis);
        }

        ModelAtom ma = getAtom(atom);
        if (ma != null) {
            return ma.manageDisclosures(dis);
        } else {
            return dis;
        }
    }

    public List<Disclosure> manageDisclosures(List<Disclosure> dis) {
        return dis;
    }

    public abstract List<Disclosure> collectDisclosure();

    public abstract void fetchDisclosures(Map<Disclosure, AbsSimModel> ds_ms, double ti) throws JSONException, IncompleteConditionException;

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

    public void initialiseObservations(double ti) throws JSONException {
        Observer.initialiseObservations(this, ti);
    }

    public void updateObservations(double ti) throws JSONException {
        Observer.observeRoutinely(this, ti);
    }

    public void captureMidTermObservations(double ti) throws JSONException {
        Observer.updateAtMidTerm(this, ti);
    }

    public void pushObservations(double ti) {
        Observer.pushObservation(ti);
    }

    public Map<String, Double> getLastObservations() {
        return Observer.getLast();
    }

    public TimeSeries outputTS() {
        return Observer.getTimeSeries();
    }

    public DataFrame outputDF() {
        return Observer.getObservations();
    }

    public Double getSnapshot(String key, double ti) {
        return Observer.getSnapshot(this, key, ti);
    }

    public void print() {
        Observer.print();
    }
}
