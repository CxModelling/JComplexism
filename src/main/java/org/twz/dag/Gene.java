package org.twz.dag;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;
import org.twz.io.IO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/21.
 */

public class Gene implements Cloneable, AdapterJSONObject {
    private double LogPriorProb, LogLikelihood;
    private Map<String, Double> Locus;

    public Gene(Map<String, Double> locus, double pp) {
        try {
            Locus = new HashMap<>(locus);
        } catch (NullPointerException e) {
            Locus = new HashMap<>();
        }

        LogPriorProb = pp;
        LogLikelihood = Double.NaN;
    }

    public Gene(Map<String, Double> locus) {
        this(locus, Double.NaN);
    }

    public Gene() {
        this(new HashMap<>(), Double.NaN);
    }

    public Map<String, Double> getLocus() {
        return Locus;
    }

    public double get(String s) {
        return Locus.get(s);
    }

    public void put(String s, double d) {
        Locus.put(s, d);
        LogLikelihood = Double.NaN;
    }

    public boolean has(String s) {
        return Locus.containsKey(s);
    }

    public void removeAll(Collection<String> ls) {
        for (String l : ls) {
            Locus.remove(l);
        }
    }

    public double getLogPriorProb() {
        return LogPriorProb;
    }

    public void setLogPriorProb(double pp) {
        LogPriorProb = pp;
    }

    public void addLogPriorProb(double p) {
        LogPriorProb += p;
    }

    public double getLogLikelihood() {
        return LogLikelihood;
    }

    public void setLogLikelihood(double logLikelihood) {
        LogLikelihood = logLikelihood;
    }

    public double getLogPosterior() {
        return LogLikelihood + LogPriorProb;
    }

    public boolean isEvaluated() {
        return !Double.isNaN(LogLikelihood);
    }

    public int getSize() {
        return Locus.size();
    }

    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        try {
            js.put("LogLikelihood", LogLikelihood);
            js.put("Values", Locus);
            js.put("LogPrior", LogPriorProb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js;
    }

    public String toString() {
        String sb = "{";
        sb += Locus.entrySet().stream()
                .map(e -> e.getKey() + ": " + IO.doubleFormat(e.getValue()))
                .collect(Collectors.joining(", "));
        sb += ", ";
        sb += "LogPrior:" + IO.doubleFormat(LogPriorProb);
        if (isEvaluated()) {
            sb += ",LogLikelihood:" + IO.doubleFormat(LogLikelihood);
        }
        sb += "}";
        return sb;
    }

    @Override
    public Gene clone() {
        return new Gene(Locus, LogPriorProb);
    }

}
