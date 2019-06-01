package org.twz.regression;

import org.apache.commons.math3.stat.StatUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ZeroInflatedCoxRegressionTest {

    ZeroInflatedCoxRegression LR;
    @Before
    public void setUp() throws Exception {
        LR = new ZeroInflatedCoxRegression(IO.loadJSON("src/test/resources/ZeroInflatedCox.json"));
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
        double k = 0;
        for (int i = 0; i < 1000; i++) {
            xs[i] = LR.predict(data);
            if (xs[i] == 0) k++;
        }
        System.out.println("Zeros: "+k);
        System.out.println(StatUtils.mean(xs));
    }
}