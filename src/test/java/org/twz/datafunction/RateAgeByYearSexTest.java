package org.twz.datafunction;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.io.IO;

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
        System.out.println(Arrays.toString(RateAge.getYears()));
    }

    @Test
    public void calculate() {
        RateAge.setParameterValue(0, 2000);
        RateAge.setParameterValue(1, 0);
        RateAge.setParameterValue(2, 20);
        System.out.println(RateAge.calculate());
    }

}