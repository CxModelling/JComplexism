package org.twz.regression;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LogisticRegressionTest {
    LogisticRegression LR;
    @Before
    public void setUp() throws Exception {
        JSONObject js = new JSONObject("{'Intercept': 0}");
        js.put("Regressors", new JSONArray("[{'Type': 'Boolean', 'Name': 'Sex', 'Value': 1.09}]"));

        LR = new LogisticRegression(js);
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
    }
}