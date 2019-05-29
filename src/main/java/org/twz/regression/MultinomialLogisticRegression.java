package org.twz.regression;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;
import org.twz.io.FnJSON;
import org.twz.prob.Sample;

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
    public double predict(Chromosome xs) {
        double[] ps = new double[Labels.length];
        ps[0] = 1;
        for (int i = 1; i < Labels.length; i++) {
            ps[i] = Math.exp(Intercepts[i] + LCs[i].findPrediction(xs));
        }

        return Sample.sample(ps);
    }

    public String getLabel(double i) {
        return Labels[(int) i];
    }
}
