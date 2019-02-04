package org.twz.dataframe;


import org.apache.commons.math3.analysis.UnivariateFunction;
import org.junit.Before;
import org.junit.Test;
import org.twz.datastructure.ProbabilityTable;
import java.util.Arrays;


public class TimeSeriesTest {

    private TimeSeries ts;

    @Before
    public void setUp() throws Exception {
        ts = TimeSeries.readCSV("src/test/resources/test_timeseries.csv", "Time");
    }

    @Test
    public void readCSV() {
        ts.print();

        System.out.println(ts.get(7));
        System.out.println(ts.get(8, "Z"));
        System.out.println(ts.get(7, "Z"));
        System.out.println(ts.get(7.5, "Z"));
    }

    @Test
    public void toPT() {
        ts.toProbabilityTable(new String[]{"X", "Y", "Z"}, new String[]{"x", "y", "z"}, "XYZ");
        ts.print();
        ProbabilityTable pt = (ProbabilityTable) ts.get(8, "XYZ");
        System.out.println(pt);
        System.out.println(Arrays.toString(pt.sample(20)));
    }

    @Test
    public void toTimeVaryingFunction() {
        UnivariateFunction fn = ts.getTimeVaryingFunction("Z");
        System.out.println(fn.value(5));
        System.out.println(fn.value(6.7));
        System.out.println(fn.value(6));
        System.out.println(fn.value(8));
    }
}