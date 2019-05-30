package org.twz.regression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;
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


    @Override
    public String getVariableType() {
        return "Binary";
    }

    @Override
    public double predict(Chromosome xs) {
        double mu = Intercept + LC.findPrediction(xs);
        if (Math.random() < (1/(1+Math.exp(-mu)))) {
            return 1.0;
        } else {
            return 0.0;
        }
    }
}
