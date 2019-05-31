package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.FnJSON;
import org.twz.prob.IDistribution;
import org.twz.regression.ZeroInflatedCoxRegression;

import java.util.HashMap;
import java.util.Map;

public class ZeroInflatedSurvival extends AbsDataFunction {
    private ZeroInflatedCoxRegression Reg;

    public ZeroInflatedSurvival(String name, JSONObject df) throws JSONException {
        super(df.getString(name), FnJSON.toStringArray(df.getJSONArray("Regressors")), df);
        Reg = new ZeroInflatedCoxRegression(df);
    }

    @Override
    public IDistribution getSampler(double[] values) {
        Map<String, Double> kvs = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            kvs.put(getParameterName(i), values[i]);
        }
        return Reg.getSampler(kvs);
    }

    @Override
    public double calculate() {
        return Double.NaN;
    }
}
