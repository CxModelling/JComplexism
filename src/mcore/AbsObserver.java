package mcore;

import utils.dataframe.DataFrame;
import java.util.*;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsObserver<T extends AbsSimModel> implements Cloneable{
    private LinkedHashMap<String, Double> Last, Mid, Flows;
    private List<Map<String, Double>> TimeSeries, TimeSeriesMid;
    private double ObservationalInterval;
    private boolean ExactMid;

    public AbsObserver(){
        TimeSeries = new LinkedList<>();
        TimeSeriesMid = new LinkedList<>();
        Flows = new LinkedHashMap<>();
        ObservationalInterval = 1;
        ExactMid = true;
        Last = new LinkedHashMap<>();
        Mid = new LinkedHashMap<>();
    }

    public void setObservationalInteval(double odt) {
        if (odt > 0) ObservationalInterval = odt;
    }

    public void setExactMid(boolean ex) {
        ExactMid = ex;
    }

    public double get(String s) {
        try {
            return Last.get(s);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void renew(){
        TimeSeries.clear();
        TimeSeriesMid.clear();
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
        readStatics(model, Last, ti);
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

    public void pushObservation(double ti) {
        Last.putAll(Flows);
        TimeSeries.add(Last);
        if (ExactMid) {
            Mid.putAll(Flows);
            TimeSeriesMid.add(Mid);
        }
        newSession(ti);
        clearFlows();
    }

    public Map<String, Double> getLast() {
        return Last;
    }

    public List<Map<String, Double>> getTimeSeries() {
        return TimeSeries;
    }

    public List<Map<String, Double>> getAdjustedTimeSeries() {
        if (TimeSeriesMid != null && TimeSeriesMid.size() == TimeSeries.size() - 1)
            return TimeSeriesMid;
        TimeSeriesMid = new LinkedList<>();
        Map<String, Double> rec;
        int n = TimeSeries.size();

        for (int i = 0; i < n - 1; i++) {
            rec = new LinkedHashMap<>();
            Map<String, Double> ts0 = TimeSeries.get(i), ts1 = TimeSeries.get(i+1);
            rec.put("Time", ts1.get("Time"));
            for (Map.Entry<String, Double> ent: ts0.entrySet()) {
                rec.put(ent.getKey(), (ent.getValue() + ts1.get(ent.getKey()))/2);
            }
            TimeSeriesMid.add(rec);
        }
        return TimeSeriesMid;
    }

    public Map<String, Double> getEntry(int i) {
        return TimeSeries.get(i);
    }

    public DataFrame getObservations() {
        return new DataFrame(TimeSeries, "Time");
    }

    public double getValue(double time, String s){
        for (Map<String, Double> data: TimeSeries){
            if (data.get("Time") == time){
                return data.get(s);
            }
        }
        return Double.NaN;
    }


    public void print(){
        StringBuilder sb = new StringBuilder();
        sb.append("Time");

        Last.keySet().stream().filter(key -> !Objects.equals(key, "Time"))
                .forEach(key -> sb.append("\t").append(key));

        for (Map<String, Double> data: TimeSeries) {
            sb.append('\n').append(String.format( "%4.1f", data.get("Time")));

            data.entrySet().stream().filter(e -> !e.getKey().equals("Time"))
                    .forEach(e -> sb.append('\t').append(String.format( "%5.3f", e.getValue())));
        }

        System.out.println(sb);
    }
}
