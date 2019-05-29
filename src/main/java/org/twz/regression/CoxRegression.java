package org.twz.regression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;
import org.twz.io.FnJSON;
import org.twz.regression.hazard.EmpiricalHazard;
import org.twz.regression.hazard.ExponentialHazard;
import org.twz.regression.hazard.IHazard;
import org.twz.regression.hazard.WeibullHazard;

import java.util.Map;


public class CoxRegression extends AbsRegression {
    private LinearCombination LC;
    private IHazard Baseline;


    public CoxRegression(JSONObject js) throws JSONException {
        Baseline = findBaseline(js.getJSONObject("Baseline"));
        LC = new LinearCombination(js.getJSONArray("Regressors"));
    }

    public CoxRegression(double baseline, JSONArray js) {
        Baseline = new ExponentialHazard(baseline);
        LC = new LinearCombination(js);
    }

    public CoxRegression(double baseline, Map<String, Double> kvs) {
        Baseline = new ExponentialHazard(baseline);
        LC = new LinearCombination(kvs);
    }

    public CoxRegression(IHazard haz, Map<String, Double> kvs) {
        Baseline = haz;
        LC = new LinearCombination(kvs);
    }

    @Override
    public String getVariableType() {
        return "TimeToEvent";
    }

    @Override
    public double predict(Chromosome chr) {
        double risk = -Math.log(Math.random())/LC.findPrediction(chr);
        return Baseline.inverseCumulativeHazard(risk);
    }

    private IHazard findBaseline(JSONObject js) throws JSONException {
        String type = js.getString("Type");

        switch (type.toLowerCase()) {
            case "exp":
                return new ExponentialHazard(js.getDouble("Rate"));
            case "weibull":
                return new WeibullHazard(js.getDouble("Lambda"), js.getDouble("K"));
            case "empirical":
                return new EmpiricalHazard(
                        FnJSON.toDoubleArray(js.getJSONArray("Time")),
                        FnJSON.toDoubleArray(js.getJSONArray("CumHaz"))
                        );
        }
        throw new JSONException("Unknown type of hazard function");
    }
}
