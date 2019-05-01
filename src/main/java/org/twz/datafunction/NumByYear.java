package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dataframe.Pair;
import org.twz.io.FnJSON;
import org.twz.prob.Binom;
import org.twz.prob.IDistribution;
import org.twz.prob.Poisson;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.sort;
import static org.apache.commons.math3.stat.StatUtils.max;
import static org.apache.commons.math3.stat.StatUtils.min;

public class NumByYear extends AbsDataFunction {

    private double[] Years;
    private double MinYear, MaxYear;


    private Map<Double, Poisson> Samplers;
    private Map<Double, Double> Data;

    public NumByYear(String name, String year_col, JSONObject df) throws JSONException {
        super(name, (new String[]{year_col}), df);

        Years = FnJSON.toDoubleArray(df.names());
        sort(Years);
        MinYear = min(Years);
        MaxYear = max(Years);

        Data = reformData(df);
        Samplers = formSamplers();
    }

    public NumByYear(String name, JSONObject df) throws JSONException {
        this(name, "Year", df);
    }

    public double[] getYears() {
        return Years;
    }

    public Pair<Double, Double> getYearRange() {
        return new Pair<>(MinYear, MaxYear);
    }

    private Map<Double, Double> reformData(JSONObject df) throws JSONException {
        Map<Double, Double> dt = new HashMap<>();

        for (String year : FnJSON.toStringArray(df.names())) {
            dt.put(Double.parseDouble(year), df.getDouble(year));
        }
        return dt;
    }

    private Map<Double, Poisson> formSamplers() {
        Map<Double, Poisson> dt = new HashMap<>();
        for (Map.Entry<Double, Double> ent : Data.entrySet()) {
            dt.put(ent.getKey(), new Poisson(ent.getValue()));
        }
        return dt;
    }

    @Override
    public double calculate() {
        double yr = Math.round(Math.max(MinYear, Math.min(MaxYear, Selected[0])));
        return Data.get(yr);
    }

    @Override
    public IDistribution getSampler(double[] values) {
        double yr = Math.round(Math.max(MinYear, Math.min(MaxYear, Selected[0])));
        return Samplers.get(yr);
    }

    @Override
    public FunctionExtension clone() {
        return null; // todo
    }
}

