package mcore;

import hgm.util.DataFrame;
import org.json.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsObserver<T extends AbsSimModel> implements Cloneable{
    protected LinkedHashMap<String, Double> Last, Current;
    private List<Map<String, Double>> TimeSeries;

    public AbsObserver(){
        TimeSeries = new ArrayList<>();
        Last = new LinkedHashMap<>();
        Current = Last;
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
        Last = new LinkedHashMap<>();
        Current = Last;
    }

    public void observeFully(AbsSimModel model, double ti) {

    }

    public abstract void initialiseObservation(T model, double ti);

    public abstract  void updateObservation(T model, double ti);

    public void pushObservation(double ti) {
        Current.put("Time", ti);
        TimeSeries.add(Current);
        Last = Current;
        Current = new LinkedHashMap<>();
    }

    public Map<String, Double> getLast() {
        return Last;
    }

    public Map<String, Double> getCurrent() {
        return Current;
    }

    public List<Map<String, Double>> getTimeSeries() {
        return TimeSeries;
    }

    public Map<String, Double> getEntry(int i) {
        return TimeSeries.get(i);
    }

    public DataFrame getObservation() {
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
