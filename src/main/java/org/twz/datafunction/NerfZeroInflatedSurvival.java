package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.FnJSON;
import org.twz.prob.Const;
import org.twz.prob.IDistribution;
import org.twz.regression.ZeroInflatedCoxRegression;

import java.util.HashMap;
import java.util.Map;

public class NerfZeroInflatedSurvival extends AbsDataFunction {
    private ZeroInflatedCoxRegression Reg;
    private double Cut;

    public NerfZeroInflatedSurvival(String name, JSONObject df1, double cut) throws JSONException {
        super(name, FnJSON.toStringArray(df1.getJSONArray("Regressors").put("year")), df1);
        Reg = new ZeroInflatedCoxRegression(df1);
        Cut = cut;
    }

    @Override
    public IDistribution getSampler(double[] values) {
        Map<String, Double> kvs = new HashMap<>();
        for (int i = 0; i < values.length-1; i++) {
            kvs.put(getParameterName(i), values[i]);
        }

        if (values[values.length-1] < Cut) {
            return Reg.getSampler(kvs);
        } else {
            return new Const(0);
        }
    }

    @Override
    public double calculate() {
        return Double.NaN;
    }
}
