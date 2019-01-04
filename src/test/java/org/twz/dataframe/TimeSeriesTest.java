package org.twz.dataframe;

import org.junit.Before;
import org.junit.Test;
import org.twz.datastructure.ProbabilityTable;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TimeSeriesTest {

    TimeSeries ts;

    @Before
    public void setUp() throws Exception {
        ts = TimeSeries.readCSV("src/test/resources/test_timeseries.csv", "Time");
    }

    @Test
    public void readCSV() {
        ts.print();
        System.out.println(ts.get(7));
        System.out.println(ts.get(8, "Z"));
    }

    @Test
    public void toPT() {
        ts.toProbabilityTable(new String[]{"X", "Y", "Z"}, new String[]{"x", "y", "z"}, "XYZ");
        ts.print();
        ProbabilityTable pt = (ProbabilityTable) ts.get(8, "XYZ");
        System.out.println(pt);
        System.out.println(Arrays.toString(pt.sample(20)));
    }
}