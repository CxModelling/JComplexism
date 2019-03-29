package org.twz.datafunction;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.io.IO;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PrAgeByYearSexTest {

    private PrAgeByYearSex PrAge;

    @Before
    public void setUp() throws JSONException {
        PrAge = new PrAgeByYearSex("YAS",
                IO.loadJSON("src/test/resources/N_ys.json"));
    }

    @Test
    public void getYears() {
        System.out.println(PrAge.getYearRange());
    }

    @Test
    public void calculate() {
        PrAge.setParameterValue(0, 2000);
        PrAge.setParameterValue(1, 0);
        System.out.println(PrAge.calculate());
    }

    @Test
    public void toJSON() throws JSONException {
        System.out.println(PrAge.toJSON());
        System.out.println(PrAge.getType());
    }
}