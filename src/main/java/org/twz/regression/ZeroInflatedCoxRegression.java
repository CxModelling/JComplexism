package org.twz.regression;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.prob.IDistribution;
import org.twz.regression.hazard.*;
import org.twz.regression.regressor.LinearCombination;

import java.util.Map;

public class ZeroInflatedCoxRegression  extends AbsRegression {
    private double Intercept;
    private IHazard Baseline;
    private LinearCombination LC_rr, LC_pr;

    public ZeroInflatedCoxRegression(JSONObject js) throws JSONException {
        JSONObject sub = js.getJSONObject("PrZero");
        Intercept = sub.getDouble("Intercept");
        LC_pr = new LinearCombination(sub.getJSONArray("Regressors"));

        sub = js.getJSONObject("PrTTE");
        Baseline = CoxRegression.findBaseline(sub.getJSONObject("Baseline"));
        LC_rr = new LinearCombination(sub.getJSONArray("Regressors"));
    }


    @Override
    public String getVariableType() {
        return "TimeToEvent";
    }

    @Override
    public double predict(Map<String, Double> xs) {
        double pr = getProbability(xs);
        if (Math.random() < pr) return 0;
        double risk = -Math.log(Math.random())/getRiskRatio(xs);
        return Baseline.inverseCumulativeHazard(risk);
    }

    @Override
    public IDistribution getSampler(Map<String, Double> xs) {
        double pr = getProbability(xs), rr = getRiskRatio(xs);
        return new ZeroInflatedHazardDist(pr, Baseline, rr);
    }

    private double getRiskRatio(Map<String, Double> xs) {
        return Math.exp(LC_rr.findPrediction(xs));
    }

    private double getProbability(Map<String, Double> kvs) {
        double mu = Intercept + LC_pr.findPrediction(kvs);
        return 1/(1+Math.exp(-mu));
    }
}
