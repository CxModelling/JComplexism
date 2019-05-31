package org.twz.regression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.prob.Binom;
import org.twz.prob.IDistribution;
import org.twz.regression.regressor.LinearCombination;

import java.util.Map;

public class LogisticRegression extends AbsRegression {
    private double Intercept;
    private LinearCombination LC;

    public LogisticRegression(JSONObject js) throws JSONException {
        Intercept = js.getDouble("Intercept");
        LC = new LinearCombination(js.getJSONArray("Regressors"));
    }

    public LogisticRegression(double intercept, JSONArray js) {
        Intercept = intercept;
        LC = new LinearCombination(js);
    }

    public LogisticRegression(double intercept, Map<String, Double> kvs) {
        Intercept = intercept;
        LC = new LinearCombination(kvs);
    }

    private double getProbability(Map<String, Double> kvs) {
        double mu = Intercept + LC.findPrediction(kvs);
        return 1/(1+Math.exp(-mu));
    }

    @Override
    public String getVariableType() {
        return "Binary";
    }

    @Override
    public double predict(Map<String, Double> xs) {
        return (Math.random() < getProbability(xs))? 1.0: 0.0;
    }

    @Override
    public IDistribution getSampler(Map<String, Double> xs) {
        double p = getProbability(xs);
        return new Binom(1, p);
    }


}
