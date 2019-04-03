package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dataframe.Pair;
import org.twz.io.FnJSON;
import org.twz.prob.Exponential;
import org.twz.prob.IDistribution;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.sort;
import static org.apache.commons.math3.stat.StatUtils.*;

public class RateAgeByYearSex extends AbsDataFunction {

    private double[] Years;
    private double MinYear, MaxYear;

    private String[] SexLabels;
    private Map<Double, Map<Integer, double[]>> Data;
    private Map<Double, Map<Integer, Map<Integer, Exponential>>> Samplers;


    public RateAgeByYearSex(String name, String[] sel_col, String[] sex_lab, JSONObject df) throws JSONException {
        super(name, sel_col, df);

        Years = FnJSON.toDoubleArray(df.names());
        sort(Years);
        MinYear = min(Years);
        MaxYear = max(Years);

        SexLabels = sex_lab;

        Data = reformData(df);
        Samplers = formSamplers();
    }

    public RateAgeByYearSex(String name, JSONObject df) throws JSONException {
        this(name, (new String[]{"Year", "Sex", "Age"}),
                (new String[]{"Female", "Male"}), df);
    }

    public double[] getYears() {
        return Years;
    }

    public Pair<Double, Double> getYearRange() {
        return new Pair<>(MinYear, MaxYear);
    }

    private Map<Double, Map<Integer, double[]>> reformData(JSONObject df) throws JSONException {
        Map<Double, Map<Integer, double[]>> dt = new HashMap<>();
        Map<Integer, double[]> ent;
        JSONObject sel;
        for (String year : FnJSON.toStringArray(df.names())) {
            ent = new HashMap<>();
            sel = df.getJSONObject(year);
            for (int i = 0; i < SexLabels.length; i++) {
                ent.put(i, FnJSON.toDoubleArray(sel.getJSONArray(SexLabels[i])));
            }
            dt.put(Double.parseDouble(year), ent);
        }
        return dt;
    }

    private Map<Double, Map<Integer, Map<Integer, Exponential>>> formSamplers() {
        Map<Double, Map<Integer, Map<Integer, Exponential>>> dt = new HashMap<>();
        Map<Integer, Map<Integer, Exponential>> s_e;
        Map<Integer, Exponential> a_e;
        double[] rates;

        for (Map.Entry<Double, Map<Integer, double[]>> y_ent : Data.entrySet()) {
            s_e = new HashMap<>();

            for (Map.Entry<Integer, double[]> s_ent : y_ent.getValue().entrySet()) {
                a_e = new HashMap<>();
                rates = s_ent.getValue();
                for (int i = 0; i < rates.length; i++) {
                    a_e.put(i, new Exponential(rates[i]));
                }
                s_e.put(s_ent.getKey(), a_e);
            }
            dt.put(y_ent.getKey(), s_e);
        }
        return dt;
    }

    @Override
    public double calculate() {
        double yr = Math.round(Math.max(MinYear, Math.min(MaxYear, Selected[0])));
        int sex = (int) Math.round(Selected[1]);
        int age = (int) Selected[2];
        return Data.get(yr).get(sex)[age];
    }

    @Override
    public IDistribution getSampler(double[] values) {
        double yr = Math.round(Math.max(MinYear, Math.min(MaxYear, Selected[0])));
        int sex = (int) Math.round(Selected[1]);
        int age = (int) Selected[2];
        return Samplers.get(yr).get(sex).get(age);
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
