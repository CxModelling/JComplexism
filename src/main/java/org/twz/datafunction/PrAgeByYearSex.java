package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;

import org.twz.dataframe.Pair;
import org.twz.io.FnJSON;
import org.twz.prob.Sample;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.sort;
import static org.apache.commons.math3.stat.StatUtils.max;
import static org.apache.commons.math3.stat.StatUtils.min;

public class PrAgeByYearSex  extends AbsDataFunction {

    private double[] Years;
    private double MinYear, MaxYear;

    private String[] SexLabels;
    private Map<Double, Map<Integer, Sample>> Data;


    public PrAgeByYearSex(String name, String[] sel_col, String[] sex_lab, JSONObject df) throws JSONException {
        super(name, sel_col, df);

        Years = FnJSON.toDoubleArray(df.names());
        sort(Years);
        MinYear = min(Years);
        MaxYear = max(Years);

        SexLabels = sex_lab;

        Data = reformData(df);
    }

    public PrAgeByYearSex(String name, JSONObject df) throws JSONException {
        this(name, (new String[]{"Year", "Sex"}),
                (new String[]{"Female", "Male"}), df);
    }

    public double[] getYears() {
        return Years;
    }

    public Pair<Double, Double> getYearRange() {
        return new Pair<>(MinYear, MaxYear);
    }

    private Map<Double, Map<Integer, Sample>> reformData(JSONObject df) throws JSONException {
        Map<Double, Map<Integer, Sample>> dt = new HashMap<>();
        Map<Integer, Sample> ent;
        JSONObject sel;
        for (String year : FnJSON.toStringArray(df.names())) {
            ent = new HashMap<>();
            sel = df.getJSONObject(year);
            for (int i = 0; i < SexLabels.length; i++) {
                ent.put(i, new Sample(FnJSON.toDoubleArray(sel.getJSONArray(SexLabels[i]))));
            }
            dt.put(Double.parseDouble(year), ent);
        }
        return dt;
    }

    @Override
    public double calculate() {
        double yr = Math.round(Math.max(MinYear, Math.min(MaxYear, Selected[0])));
        int sex = (int) Math.round(Selected[1]);
        return Data.get(yr).get(sex).sample();
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
