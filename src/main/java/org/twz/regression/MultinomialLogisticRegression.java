package org.twz.regression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.FnJSON;
import org.twz.prob.IDistribution;
import org.twz.prob.Sample;
import org.twz.regression.regressor.LinearCombination;

import java.util.Map;


public class MultinomialLogisticRegression extends AbsRegression {

    private String[] Labels;
    private LinearCombination[] LCs;
    private double[] Intercepts;

    public MultinomialLogisticRegression(JSONObject js) throws JSONException {
        Intercepts = FnJSON.toDoubleArray(js.getJSONArray("Intercepts"));
        if (js.has("Labels")) {
            Labels = FnJSON.toStringArray(js.getJSONArray("Labels"));
        } else {
            Labels = new String[Intercepts.length];
            for (int i = 0; i < Intercepts.length; i++) {
                Labels[i] = "" + i;
            }
        }

        LCs = new LinearCombination[Intercepts.length];
        JSONArray rs = js.getJSONArray("Regressions");
        for (int i = 1; i < rs.length(); i++) {
            LCs[i] = new LinearCombination(rs.getJSONArray(i));
        }
    }


    @Override
    public String getVariableType() {
        return "Category";
    }

    @Override
    public double predict(Map<String, Double> xs) {
        return Sample.sample(getProbabilities(xs));
    }

    @Override
    public IDistribution getSampler(Map<String, Double> xs) {
        return new IDistribution() {
            private double[] prs = getProbabilities(xs);
            @Override
            public String getName() {
                return "Multinomial";
            }

            @Override
            public String getDataType() {
                return "Integer";
            }

            @Override
            public double logProb(double rv) {
                return Math.log(prs[(int)rv]);
            }

            @Override
            public double sample() {
                return Sample.sample(getProbabilities(xs));
            }

            @Override
            public double[] sample(int n) {
                double[] ys = new double[n];
                int[] is = Sample.sample(prs, n);
                for (int i = 0; i < n; i++) {
                    ys[i] = is[i];
                }
                return ys;
            }
        };
    }

    private double[] getProbabilities(Map<String, Double> xs) {
        double[] ps = new double[Labels.length];
        ps[0] = 1;
        for (int i = 1; i < Labels.length; i++) {
            ps[i] = Math.exp(Intercepts[i] + LCs[i].findPrediction(xs));
        }
        return ps;
    }

    public String getLabel(double i) {
        return Labels[(int) i];
    }
}
