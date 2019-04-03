package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.io.AdapterJSONObject;
import org.twz.prob.IDistribution;


public abstract class AbsDataFunction implements FunctionExtension, AdapterJSONObject {

    private final String Name;
    protected final String[] Selectors;
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
}
