package org.twz.datafunction;

import org.apache.commons.math3.stat.StatUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.dataframe.demographics.ISampler;
import org.twz.io.IO;
import org.twz.prob.IDistribution;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RateAgeByYearSexTest {

    private RateAgeByYearSex RateAge;

    @Before
    public void setUp() throws JSONException {
        RateAge = new RateAgeByYearSex("YAS",
                IO.loadJSON("src/test/resources/D_ys.json"));
    }

    @Test
    public void getYears() {
        System.out.println(RateAge.getYearRange());
    }

    @Test
    public void calculate() {
        RateAge.setParameterValue(0, 2000);
        RateAge.setParameterValue(1, 0);
        RateAge.setParameterValue(2, 20);
        System.out.println(RateAge.calculate());
    }

    @Test
    public void sample() {
        IDistribution samp = RateAge.getSampler(new double[]{2000, 0, 85});

        RateAge.setParameterValue(0, 2000);
        RateAge.setParameterValue(1, 0);
        RateAge.setParameterValue(2, 85);
        System.out.println(1/RateAge.calculate());
        System.out.println(StatUtils.mean(samp.sample(10000)));

    }


}