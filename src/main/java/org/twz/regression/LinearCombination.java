package org.twz.regression;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinearCombination {

    private List<IRegressor> Regressors;

    public LinearCombination(JSONArray js) {
        Regressors = new ArrayList<>();
        for (int i = 0; i < js.length(); i++) {
            try {
                Regressors.add(readRegressor(js.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public LinearCombination(Map<String, Double> kvs) {
        Regressors = new ArrayList<>();
        for (Map.Entry<String, Double> ent : kvs.entrySet()) {
            Regressors.add(new ContinuousRegressor(ent.getKey(), ent.getValue()));
        }
    }

    public double findPrediction(Chromosome chr) {
        return Regressors.stream()
                .mapToDouble(r->r.getEffect(chr.getDouble(r.getName())))
                .sum();
    }


    private IRegressor readRegressor(JSONObject reg) {
        return null;
    }
}
