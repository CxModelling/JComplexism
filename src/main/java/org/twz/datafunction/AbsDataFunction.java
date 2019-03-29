package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dataframe.DataFrame;
import org.twz.io.AdapterJSONObject;


public abstract class AbsDataFunction implements FunctionExtension, AdapterJSONObject {

    private final String Name;
    protected final String[] Selectors;
    protected String[] Selected;
    private DataFrame RawData;

    public AbsDataFunction(String name, String[] sel_cols, DataFrame df) {
        Name = name;
        Selectors = sel_cols;
        Selected = new String[sel_cols.length];
        RawData = df;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public String getName() {
        return Name;
    }

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

    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public FunctionExtension clone() {
        return null;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", getName());
        js.put("Type", getType());
        js.put("Selectors", Selectors);
        js.put("RawData", RawData.toJSON());
        return js;
    }
}
