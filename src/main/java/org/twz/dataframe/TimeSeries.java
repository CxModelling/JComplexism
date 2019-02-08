package org.twz.dataframe;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.twz.dataframe.timeseries.DoubleSeries;
import org.twz.dataframe.timeseries.ProbabilityTableSeries;
import org.twz.dataframe.timeseries.Series;
import org.twz.dataframe.timeseries.StringSeries;
import org.twz.exception.TimeseriesException;
import org.twz.io.AdapterJSONArray;
import org.twz.io.IO;

import java.util.*;
import java.util.stream.Collectors;

public class TimeSeries implements AdapterJSONArray {
    private final List<Double> Times;
    private UnivariateFunction TimeFunction;
    private Map<String, Series> DataSeries;

    public TimeSeries(List<Double> times) {
        Times = times;
        TimeFunction = getTimeFunction(times);
        DataSeries = new LinkedHashMap<>();
    }

    public int getNumRow() {
        return Times.size();
    }

    public int getNumColumn() {
        return DataSeries.size();
    }

    public double getStartTime() {
        return Times.get(0);
    }

    public double getEndTime() {
        return Times.get(Times.size()-1);
    }

    public Map<String, Object> get(double time) {
        double i = TimeFunction.value(time);
        i = Math.min(i, Times.size() - 1);
        i = Math.max(i, 0);

        if (i == Math.round(i)) {
            return geti((int) i);
        } else {
            double t0 = Times.get((int) Math.floor(i)), t1 = Times.get((int) Math.ceil(i));
            Map<String, Object> res0 = geti((int) Math.floor(i)),
                                res1 = geti((int) Math.ceil(i));

            Map<String, Object> res = new HashMap<>();
            for (Series value : DataSeries.values()) {
                String key = value.getName();
                res.put(key, value.interpolate(time, t0, res0.get(key), t1, res1.get(key)));
            }
            return res;
        }
    }

    private Map<String, Object> geti(int i) {
        Map<String, Object> res = new HashMap<>();
        for (Series value : DataSeries.values()) {
            res.put(value.getName(), value.get(i));
        }
        return res;
    }

    public Object get(double time, String x) {
        double i = TimeFunction.value(time);
        i = Math.min(i, Times.size() - 1);
        i = Math.max(i, 0);

        if (Math.round(i) == i) {
            return DataSeries.get(x).get((int) i);
        } else {
            double t0 = Times.get((int) Math.floor(i)), t1 = Times.get((int) Math.ceil(i));
            Object res0 = DataSeries.get(x).get((int) Math.floor(i)),
                    res1 = DataSeries.get(x).get((int) Math.ceil(i));
            return DataSeries.get(x).interpolate(time, t0, res0, t1, res1);
        }
    }

    public Double getDouble(double time, String x) throws TimeseriesException {
        double i = TimeFunction.value(time);
        i = Math.min(i, Times.size() - 1);
        i = Math.max(i, 0);

        if (Math.round(i) == i) {
            return DataSeries.get(x).getDouble((int) i);
        } else {
            double t0 = Times.get((int) Math.floor(i)), t1 = Times.get((int) Math.ceil(i));
            double res0 = DataSeries.get(x).getDouble((int) Math.floor(i)),
                    res1 = DataSeries.get(x).getDouble((int) Math.ceil(i));
            return (double) DataSeries.get(x).interpolate(time, t0, res0, t1, res1);
        }
    }

    public UnivariateFunction getTimeVaryingFunction(String s) throws ClassCastException{
        Series series = DataSeries.get(s);
        if (series instanceof DoubleSeries) {
            double[] ys = new double[series.size()], ts = new double[series.size()];
            for (int i = 0; i < series.size(); i++) {
                try {
                    ys[i] = series.getDouble(i);
                    ts[i] = Times.get(i);
                } catch (TimeseriesException ignore) {

                }
            }
            return (new LinearInterpolator()).interpolate(ts, ys);
        } else {
            throw new ClassCastException();
        }
    }

    public TimeSeries separateSeries(String x) {
        TimeSeries ts = new TimeSeries(Times);
        ts.appendSeries(DataSeries.get(x));
        return ts;
    }

    protected void appendSeries(Series series) {
        DataSeries.put(series.getName(), series);
    }

    public void toProbabilityTable(String[] xs, String[] labels, String y) {
        assert xs.length == labels.length;

        List<Series> src = new ArrayList<>();
        for (String x: xs) {
            src.add(DataSeries.remove(x));
        }

        List<double[]> ds = new ArrayList<>();
        extractPT(xs, src, ds);

        DataSeries.put(y, new ProbabilityTableSeries(y, labels, ds));
    }

    public void addProbabilityTable(String[] xs, String[] labels, String y) {
        assert xs.length == labels.length;

        List<Series> src = new ArrayList<>();
        for (String x: xs) {
            src.add(DataSeries.get(x));
        }

        List<double[]> ds = new ArrayList<>();
        extractPT(xs, src, ds);

        DataSeries.put(y, new ProbabilityTableSeries(y, labels, ds));
    }

    private void extractPT(String[] xs, List<Series> src, List<double[]> ds) {
        double[] d;
        for (int i = 0; i < getNumRow(); i++) {
            d = new double[xs.length];
            for (int j = 0; j < src.size(); j++) {
                d[j] = (double) src.get(j).get(i);
            }
            ds.add(d);
        }
    }


    public void print() {
        System.out.println(
                "Timeseries [" + Times.get(0) + ", " +
                        Times.get(Times.size()-1)+ "]");
        System.out.println("Time " + String.join("\t", DataSeries.keySet()));

        for (double t: Times) {
            System.out.print(t);
            for (String k : DataSeries.keySet()) {
                System.out.print(" " + get(t, k));
            }
            System.out.println();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Timeseries [").append(Times.get(0))
                .append(", ")
                .append(Times.get(Times.size() - 1))
                .append("]");

        for(Map.Entry<String, Series> entry: DataSeries.entrySet()) {
            sb.append("\n");
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().get(0).getClass().getSimpleName());
        }

        return sb.toString();
    }

    @Override
    public JSONArray toJSON() throws JSONException {
        return null;
    }

    public static TimeSeries readCSV(String file_path, String i_time) {
        Map<String, List<String>> data = IO.loadCSV(file_path);

        TimeSeries ts = new TimeSeries(SS2DS(data.get(i_time)));

        for (Map.Entry<String, List<String>> ent: data.entrySet()) {
            if (ent.getKey().equals(i_time)) continue;
            try {
                ts.appendSeries(new DoubleSeries(ent.getKey(), SS2DS(ent.getValue())));
            } catch (ClassCastException e) {
                ts.appendSeries(new StringSeries(ent.getKey(), ent.getValue()));
            }
        }
        return ts;
    }

    private static List<Double> SS2DS(List<String> ss) {
        List<Double> res = new ArrayList<>();
        for (String s: ss) {
            res.add(Double.parseDouble(s));
        }
        return res;
    }

    private static UnivariateFunction getTimeFunction(List<Double> ts) {
        double[] t = new double[ts.size()];
        double[] i = new double[ts.size()];

        for (int j = 0; j < ts.size(); j++) {
            t[j] = ts.get(j);
            i[j] = j;
        }
        return (new LinearInterpolator()).interpolate(t, i);
    }
}
