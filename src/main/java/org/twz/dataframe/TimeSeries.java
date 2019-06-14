package org.twz.dataframe;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dataframe.timeseries.DoubleSeries;
import org.twz.dataframe.timeseries.ProbabilityTableSeries;
import org.twz.dataframe.timeseries.Series;
import org.twz.dataframe.timeseries.StringSeries;
import org.twz.exception.TimeseriesException;
import org.twz.io.AdapterJSONObject;
import org.twz.io.IO;

import java.util.*;
import java.util.stream.Collectors;

public class TimeSeries implements AdapterJSONObject {
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

    public Set<String> getColumnName() {
        return DataSeries.keySet();
    }

    public double getStartTime() {
        return Times.get(0);
    }

    public double getEndTime() {
        return Times.get(Times.size()-1);
    }

    public List<Double> getTimes() {
        return Times;
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

            Map<String, Object> res = new LinkedHashMap<>();
            for (Series value : DataSeries.values()) {
                String key = value.getName();
                res.put(key, value.interpolate(time, t0, res0.get(key), t1, res1.get(key)));
            }
            return res;
        }
    }

    private Map<String, Object> geti(int i) {
        Map<String, Object> res = new LinkedHashMap<>();
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
        double i;
        try {
            i = TimeFunction.value(time);
        } catch (OutOfRangeException e) {
            i = Times.size() - 1;
        }

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
        System.out.println(String.format("%7s", "Time") + "  " +
                        DataSeries.keySet().stream()
                                .map(d->String.format("%7s", d))
                                .collect(Collectors.joining("  ")));

        for (double t: Times) {
            System.out.printf("%7f", t);
            for (String k : DataSeries.keySet()) {
                System.out.print("  ");
                try {
                    System.out.printf("%7g", (Double) get(t, k));
                } catch (IllegalFormatConversionException e) {
                    System.out.print("[" + get(t, k) + "]");
                }

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

    public void toJSON(String path) throws JSONException {
        IO.writeText(toJSON().toString(), path);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Time", Times);
        for (Map.Entry<String, Series> ent : DataSeries.entrySet()) {
            js.put(ent.getKey(), ent.getValue());
        }
        return js;
    }

    public void toCSV(String path) {
        StringBuilder sb = new StringBuilder();

        sb.append("Time");

        DataSeries.keySet().forEach(k->sb.append(",").append(k));
        for (int i = 0; i < Times.size(); i++) {
            sb.append("\n");
            sb.append(Times.get(i));
            geti(i).values().forEach(v->sb.append(",").append(v));
        }

        IO.writeText(sb.toString(), path);
    }

    public static TimeSeries readCSV(String file_path, String i_time) {
        Map<String, List<String>> data = IO.loadCSV(file_path);

        TimeSeries ts = new TimeSeries(SS2DS(data.get(i_time)));

        for (Map.Entry<String, List<String>> ent: data.entrySet()) {
            if (ent.getKey().equals(i_time)) continue;
            try {
                ts.appendSeries(new DoubleSeries(ent.getKey(), SS2DS(ent.getValue())));
            } catch (NumberFormatException e) {
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

    public static Map<String, TimeSeries> transpose(Map<String, TimeSeries> timeSeriesMap) {
        Map<String, TimeSeries> res = new LinkedHashMap<>();

        TimeSeries first;
        first = timeSeriesMap.entrySet().stream().findFirst().map(Map.Entry::getValue).orElse(null);
        assert first != null;

        List<String> cols = new ArrayList<>(first.getColumnName());

        for (String col : cols) {
            TimeSeries ts = new TimeSeries(first.Times);
            for (Map.Entry<String, TimeSeries> ent : timeSeriesMap.entrySet()) {
                ts.appendSeries(new Series(ent.getKey(), ent.getValue().DataSeries.get(col)));
            }

            res.put(col, ts);
        }
        return res;
    }

    public static Map<Double, Map<String, Double>> combineAllNumbers(TimeSeries ts1, TimeSeries ts2) {
        Map<Double, Map<String, Double>> res = new HashMap<>();
        Set<Double> times = new HashSet<>(ts1.Times);
        times.retainAll(ts2.Times);

        Map<String, Double> entry;

        for (Double time : times) {
            entry = new HashMap<>();
            for (String s : ts1.getColumnName()) {
                try {
                    entry.put(s, ts1.getDouble(time, s));
                } catch (TimeseriesException ignored) {

                }
            }
            for (String s : ts2.getColumnName()) {
                try {
                    entry.put(s, ts2.getDouble(time, s));
                } catch (TimeseriesException ignored) {

                }
            }
            res.put(time, entry);
        }
        return res;
    }
}
