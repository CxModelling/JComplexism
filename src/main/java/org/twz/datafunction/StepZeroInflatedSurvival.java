package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.FnJSON;
import org.twz.prob.IDistribution;
import org.twz.regression.ZeroInflatedCoxRegression;

import java.util.HashMap;
import java.util.Map;

public class StepZeroInflatedSurvival extends AbsDataFunction {
    private ZeroInflatedCoxRegression Reg1, Reg2;
    private double Cut;

    public StepZeroInflatedSurvival(String name, JSONObject df1, JSONObject df2, double cut) throws JSONException {
        super(name, FnJSON.toStringArray(df1.getJSONArray("Regressors")), df1);
        Reg1 = new ZeroInflatedCoxRegression(df1);
        Reg2 = new ZeroInflatedCoxRegression(df2);
        Cut = cut;
    }

    @Override
    public IDistribution getSampler(double[] values) {
        Map<String, Double> kvs = new HashMap<>();
        for (int i = 0; i < values.length-1; i++) {
            kvs.put(getParameterName(i), values[i]);
        }

        if (values[values.length-1] < Cut) {
            return Reg1.getSampler(kvs);
        } else {
            return Reg2.getSampler(kvs);
        }
    }

    @Override
    public double calculate() {
        return Double.NaN;
    }
}
