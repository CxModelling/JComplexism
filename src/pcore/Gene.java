package pcore;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/21.
 */

public class Gene implements Cloneable {
    private double LogPriorProb, LogLikelihood;
    private Map<String, Double> Locus;

    public Gene(Map<String, Double> locus, double pp) {
        Locus = new HashMap<>(locus);
        LogPriorProb = pp;
        LogLikelihood = 0;
    }

    public Gene(Map<String, Double> locus) {
        this(locus, 0);
    }

    public Gene() {
        this(new HashMap<>(), 0);
    }

    public Map<String, Double> getLocus() {
        return Locus;
    }

    public double get(String s) {
        return Locus.get(s);
    }

    public void put(String s, double d) {
        Locus.put(s, d);
        LogLikelihood = 0;
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
        return LogLikelihood != 0;
    }

    public int getSize() {
        return Locus.size();
    }

    public String toJSON() {
        String sb = "{Values:{";
        sb += Locus.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));
        sb += "}, ";
        sb += "LogPrior:" + LogPriorProb;
        if (isEvaluated()) {
            sb += ",LogLikelihood:" + LogLikelihood;
        }
        sb += "}";
        return sb;
    }

    public String toString() {
        String sb = "Gene{";
        sb += Locus.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(","));
        sb += "}, ";
        sb += "LogPrior:" + LogPriorProb;
        if (isEvaluated()) {
            sb += ",LogLikelihood:" + LogLikelihood;
        }
        sb += "}";
        return sb;
    }

    @Override
    public Gene clone() {
        return new Gene(Locus, LogPriorProb);
    }

}
