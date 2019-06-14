package org.twz.cx.mcore;

import org.twz.dataframe.DataFrame;
import org.twz.dataframe.TimeSeries;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsObserver<T extends AbsSimModel> implements Cloneable{
    protected LinkedHashMap<String, Double> Last;
    private LinkedHashMap<String, Double> Mid;
    private LinkedHashMap<String, Double> Flows;
    private FnSummary Summariser;

    private List<Map<String, Double>> Observation, ObservationMid;
    private List<String> StockNames, FlowNames;
    private LinkedHashMap<String, Double> Snapshot;

    protected double ObservationalInterval;
    private boolean ExactMid;

    protected AbsObserver(){
        Observation = new LinkedList<>();
        ObservationMid = new LinkedList<>();
        ObservationalInterval = 1;
        ExactMid = true;
        Last = new LinkedHashMap<>();
        Mid = new LinkedHashMap<>();
        Flows = new LinkedHashMap<>();
        Snapshot = new LinkedHashMap<>();
        Snapshot.put("Time", Double.MIN_VALUE);

        Summariser = (tab, model, ti) -> { };
    }

    public void putAllFlows(String prefix, Map<String, Double> dis) {
        Flows.forEach((k, v) -> dis.put(prefix + ":" + k, v));
    }

    public void putAllLast(String prefix, T model, Map<String, Double> dis) {
        Last.forEach((k, v) -> {
            if (!k.equals("Time")) dis.put(prefix + ":" + k, v);
        });
    }

    public void putAllMid(String prefix, T model, Map<String, Double> dis) {
        Mid.forEach((k, v) -> {
            if (!k.equals("Time")) dis.put(prefix + ":" + k, v);
        });
    }

    public void setObservationalInterval(double odt) {
        assert odt > 0;
        ObservationalInterval = odt;
    }

    public void setSummariser(FnSummary summary) {
        if (summary == null) return;
        Summariser = summary;
    }

    public void setExactMid(boolean ex) {
        ExactMid = ex;
    }

    void renew(){
        Observation.clear();
        ObservationMid.clear();
    }

    private void newSession(double ti) {
        Last = new LinkedHashMap<>();
        Last.put("Time", ti+ObservationalInterval);
        if (ExactMid) {
            Mid = new LinkedHashMap<>();
            Mid.put("Time", ti+ObservationalInterval);
        }
        Flows.clear();
    }

    public void initialiseObservations(T model, double ti) {
        renew();
        clearFlows();
        Last.put("Time", ti);
        if (ExactMid) {
            Mid = new LinkedHashMap<>();
            Mid.put("Time", ti);
        }
        readStatics(model, Last, ti);
        updateDynamicObservations(model, Flows, ti);
        StockNames = new ArrayList<>(Last.keySet());
        FlowNames = new ArrayList<>(Flows.keySet());
    }

    public void observeRoutinely(T model, double ti) {
        readStatics(model, Last, ti);
        updateDynamicObservations(model, Flows, ti);
    }

    public void updateAtMidTerm(T model, double ti) {
        if (ExactMid) readStatics(model, Mid, ti);
    }

    protected abstract void readStatics(T model, Map<String, Double> tab, double ti);

    public abstract void updateDynamicObservations(T model, Map<String, Double> flows, double ti);

    protected void clearFlows() {
        Flows.clear();
    }

    void pushObservation(T model, double ti) {
        Observation.add(getLast(model, ti));
        if (ExactMid) {
            ObservationMid.add(getMid(model, ti));
        }
        newSession(ti);
        clearFlows();
    }

    public Map<String, Double> getLast(T model, double ti) {
        Map<String, Double> dat = new HashMap<>();
        dat.putAll(Last);
        dat.putAll(Flows);
        Summariser.call(dat, model, ti);
        return dat;
    }

    public Map<String, Double> getMid(T model, double ti) {
        Map<String, Double> dat = new HashMap<>();
        dat.putAll(Mid);
        dat.putAll(Flows);
        Summariser.call(dat, model, ti);
        return dat;
    }

    public TimeSeries getTimeSeries() {
        return getObservations().toTimeSeries();
    }

    public TimeSeries getAdjustedTimeSeries() {
        if (ObservationMid != null && ObservationMid.size() == Observation.size() - 1)
            return (new DataFrame(ObservationMid, "Time")).toTimeSeries();
        ObservationMid = new LinkedList<>();
        Map<String, Double> rec;
        int n = Observation.size();

        for (int i = 0; i < n - 1; i++) {
            rec = new LinkedHashMap<>();
            Map<String, Double> ts0 = Observation.get(i), ts1 = Observation.get(i+1);
            rec.put("Time", ts1.get("Time"));
            for (Map.Entry<String, Double> ent: ts0.entrySet()) {
                rec.put(ent.getKey(), (ent.getValue() + ts1.get(ent.getKey()))/2);
            }
            ObservationMid.add(rec);
        }
        return (new DataFrame(ObservationMid, "Time")).toTimeSeries();
    }

    public Map<String, Double> getEntry(int i) {
        return Observation.get(i);
    }

    public DataFrame getObservations() {
        return new DataFrame(Observation, "Time");
    }

    public double getValue(double time, String s){
        for (Map<String, Double> data: Observation){
            if (data.get("Time") == time){
                return data.get(s);
            }
        }
        return Double.NaN;
    }

    public double getSnapshot(T model, String key, double ti) {
        if (FlowNames.contains(key)) {
            return Observation.get(Observation.size() - 1).get(key);
        } else if (StockNames.contains(key)) {
            if (ti > Snapshot.get("Time") || !Snapshot.containsKey(key)) {
                Snapshot.clear();
                readStatics(model, Snapshot, ti);
                Snapshot.put("Time", ti);
            }
            return Snapshot.get(key);
        }
        return Double.NaN;
    }


    public void print(){
        getObservations().println();
    }
}
