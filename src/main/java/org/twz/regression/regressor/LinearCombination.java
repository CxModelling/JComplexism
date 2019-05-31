package org.twz.regression.regressor;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.FnJSON;

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

    public double findPrediction(Map<String, Double> xs) {
        return Regressors.stream()
                .mapToDouble(r->r.getEffect(xs.get(r.getName())))
                .sum();
    }

    public String[] getRegressorList() {
        return Regressors.stream().map(IRegressor::getName).toArray(String[]::new);
    }

    private IRegressor readRegressor(JSONObject reg) throws JSONException {
        String type = reg.getString("Type");
        switch (type) {
            case "Boolean":
                return new BooleanRegressor(reg.getString("Name"), reg.getDouble("Value"));
            case "Categorical":
                return new CategoricalRegressor(reg.getString("Name"),
                        FnJSON.toDoubleArray(reg.getJSONArray("Values")),
                        FnJSON.toStringArray(reg.getJSONArray("Labels")),
                        reg.getString("Ref"));
            default:
                return new ContinuousRegressor(reg.getString("Name"), reg.getDouble("Value"));
        }
    }
}
