package org.twz.regression;

import org.apache.commons.math3.stat.StatUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CoxRegressionTest {
    CoxRegression LR;

    @Before
    public void setUp() throws Exception {
        JSONObject js = new JSONObject();
        js.put("Baseline", new JSONObject("{'Type': 'Weibull', 'Lambda': 0.02, 'K': 0.5}"));
        js.put("Regressors", new JSONArray("[{'Type': 'Boolean', 'Name': 'Sex', 'Value': 0.69}]"));

        LR = new CoxRegression(js);
    }

    @Test
    public void predict() {
        Map<String, Double> data = new HashMap<>();
        data.put("Sex", 1.0);
        System.out.println(LR.predict(data));
    }

    @Test
    public void predictN() {
        Map<String, Double> data = new HashMap<>();
        data.put("Sex", 1.0);
        double[] xs = new double[1000];
        for (int i = 0; i < 1000; i++) {
            xs[i] = LR.predict(data);
        }
        System.out.println(StatUtils.mean(xs));

        data.put("Sex", 0.0);
        xs = new double[1000];
        for (int i = 0; i < 1000; i++) {
            xs[i] = LR.predict(data);
        }
        System.out.println(StatUtils.mean(xs));
    }
}