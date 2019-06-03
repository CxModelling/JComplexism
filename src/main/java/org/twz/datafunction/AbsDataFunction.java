package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.io.AdapterJSONObject;
import org.twz.io.FnJSON;
import org.twz.prob.IDistribution;

import java.util.HashMap;
import java.util.Map;


public abstract class AbsDataFunction implements FunctionExtension, AdapterJSONObject {

    private final String Name;
    private final String[] Selectors;
    protected double[] Selected;
    private JSONObject RawData;

    public AbsDataFunction(String name, String[] sel_cols, JSONObject df) {
        Name = name;
        Selectors = sel_cols;
        Selected = new double[sel_cols.length];
        RawData = df;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public String getName() {
        return Name;
    }

    public abstract IDistribution getSampler(double[] values);

    @Override
    public int getParametersNumber() {
        return Selectors.length;
    }

    @Override
    public String getParameterName(int i) {
        return Selectors[i];
    }

    @Override
    public void setParameterValue(int i, double v) {
        Selected[i] = v;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", getName());
        js.put("Type", getType());
        js.put("Selectors", Selectors);
        js.put("RawData", RawData);
        return js;
    }

    @Override
    public FunctionExtension clone() {
        return null;
    }


    static Map<Double, Map<Integer, double[]>> toTimeSexAgeData(String[] sexes, JSONObject df) throws JSONException {
        Map<Double, Map<Integer, double[]>> dt = new HashMap<>();
        Map<Integer, double[]> ent;
        JSONObject sel;
        for (String year : FnJSON.toStringArray(df.names())) {
            ent = new HashMap<>();
            sel = df.getJSONObject(year);
            for (int i = 0; i < sexes.length; i++) {
                ent.put(i, FnJSON.toDoubleArray(sel.getJSONArray(sexes[i])));
            }
            dt.put(Double.parseDouble(year), ent);
        }
        return dt;
    }

    static Map<Double, Map<Integer, Double>> toTimeSexData(String[] sexes, JSONObject df) throws JSONException {
        Map<Double, Map<Integer, Double>> dt = new HashMap<>();
        Map<Integer, Double> ent;
        JSONObject sel;
        for (String year : FnJSON.toStringArray(df.names())) {
            ent = new HashMap<>();
            sel = df.getJSONObject(year);
            for (int i = 0; i < sexes.length; i++) {
                ent.put(i, sel.getDouble(sexes[i]));
            }
            dt.put(Double.parseDouble(year), ent);
        }
        return dt;
    }
}
