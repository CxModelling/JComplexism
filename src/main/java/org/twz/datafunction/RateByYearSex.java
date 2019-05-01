package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dataframe.Pair;
import org.twz.io.FnJSON;
import org.twz.prob.Exponential;
import org.twz.prob.IDistribution;
import org.twz.util.Misc;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.sort;
import static org.apache.commons.math3.stat.StatUtils.max;
import static org.apache.commons.math3.stat.StatUtils.min;

public class RateByYearSex  extends AbsDataFunction {

    private double[] Years;
    private double MinYear, MaxYear;

    private String[] SexLabels;
    private Map<Double, Map<Integer, Double>> Data;
    private Map<Double, Map<Integer, Exponential>> Samplers;


    public RateByYearSex(String name, String[] sel_col, String[] sex_lab, JSONObject df) throws JSONException {
        super(name, sel_col, df);

        Years = FnJSON.toDoubleArray(df.names());
        sort(Years);
        MinYear = min(Years);
        MaxYear = max(Years);

        SexLabels = sex_lab;

        Data = reformData(df);
        Samplers = formSamplers();

    }

    public RateByYearSex(String name, JSONObject df) throws JSONException {
        this(name, (new String[]{"Year", "Sex"}),
                (new String[]{"Female", "Male"}), df);
    }

    public double[] getYears() {
        return Years;
    }

    public Pair<Double, Double> getYearRange() {
        return new Pair<>(MinYear, MaxYear);
    }

    private Map<Double, Map<Integer, Double>> reformData(JSONObject df) throws JSONException {
        return AbsDataFunction.toTimeSexData(SexLabels, df);
    }

    private Map<Double, Map<Integer, Exponential>> formSamplers() {
        Map<Double, Map<Integer, Exponential>> dt = new HashMap<>();
        Map<Integer, Exponential> s_e;

        for (Map.Entry<Double, Map<Integer, Double>> y_ent : Data.entrySet()) {
            s_e = new HashMap<>();

            for (Map.Entry<Integer, Double> s_ent : y_ent.getValue().entrySet()) {
                s_e.put(s_ent.getKey(), new Exponential(s_ent.getValue()));
            }
            dt.put(y_ent.getKey(), s_e);
        }
        return dt;
    }

    @Override
    public double calculate() {
        double yr = java.lang.Math.round(java.lang.Math.max(MinYear, java.lang.Math.min(MaxYear, Selected[0])));
        int sex = (int) java.lang.Math.round(Selected[1]);
        return Data.get(yr).get(sex);
    }

    @Override
    public IDistribution getSampler(double[] values) {
        double yr = Misc.frame(Selected[0], MinYear, MaxYear);
        int sex = Selected[1] > 0? 1:0;
        return Samplers.get(yr).get(sex);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("SexLabel", SexLabels);
        return js;
    }

    @Override
    public FunctionExtension clone() {
        return null; // todo
    }
}

