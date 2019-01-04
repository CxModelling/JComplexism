package org.twz.dataframe;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.twz.dataframe.timeseries.DoubleSeries;
import org.twz.dataframe.timeseries.ProbabilityTableSeries;
import org.twz.dataframe.timeseries.Series;
import org.twz.dataframe.timeseries.StringSeries;
import org.twz.io.AdapterJSONArray;
import org.twz.io.IO;

import java.util.*;

public class TimeSeries implements AdapterJSONArray {
    private final List<Double> Times;
    private UnivariateFunction TimeFunction;
    private Map<String, Series> DataSeries;

    public TimeSeries(List<Double> times) {
        Times = times;
        TimeFunction = getTimeFunction(times);
        DataSeries = new LinkedHashMap<>();
    }

    public int nrow() {
        return Times.size();
    }

    public int ncol() {
        return DataSeries.size();
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

        if (i == Math.round(i)) {
            return DataSeries.get(x).get((int) i);
        } else {
            double t0 = Times.get((int) Math.floor(i)), t1 = Times.get((int) Math.ceil(i));
            Object res0 = DataSeries.get(x).get((int) Math.floor(i)),
                    res1 = DataSeries.get(x).get((int) Math.ceil(i));

            return DataSeries.get(x).interpolate(time, t0, res0, t1, res1);
        }
    }

    public Series getSeries(String x) {
        return DataSeries.get(x);
    }

    public void appendSeries(Series series) {
        DataSeries.put(series.getName(), series);
    }

    public void toProbabilityTable(String[] xs, String[] labels, String y) {
        assert xs.length == labels.length;

        List<Series> src = new ArrayList<>();
        for (String x: xs) {
            src.add(DataSeries.remove(x));
        }

        List<double[]> ds = new ArrayList<>();
        double[] d;
        for (int i = 0; i < nrow(); i++) {
            d = new double[xs.length];
            for (int j = 0; j < src.size(); j++) {
                d[j] = (double) src.get(j).get(i);
            }
            ds.add(d);
        }

        DataSeries.put(y, new ProbabilityTableSeries(y, labels, ds));
    }


    public void print() {
        System.out.println(
                "Timeseries [" + Times.get(0) + ", " +
                        Times.get(Times.size()-1)+ "]");
        for(Map.Entry<String, Series> entry: DataSeries.entrySet()) {
            System.out.println(entry.getKey() + ": " +
                    entry.getValue().get(0).getClass().getSimpleName());
        }
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
