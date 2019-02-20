package org.twz.prob;


import org.twz.util.Statistics;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * Created by TimeWz on 2016/7/13.
 */
public class StatisticsTest {

    double[] arr = {1, 2, 3, 5, 6};

    @Test
    public void testSeq() {
        System.out.println(Arrays.toString(Statistics.seq(0, 10, 4)));
        System.out.println(Arrays.toString(Statistics.seq(0, 10, 2)));
    }

    @Test
    public void testMax() {
        assertEquals(Statistics.max(arr), 6.0);
    }

    @Test
    public void testArgmax() {
        assertEquals(Statistics.argmax(arr), 4);
    }

    @Test
    public void testMin() {
        assertEquals(Statistics.min(arr), 1.0);
    }

    @Test
    public void testArgmin() {
        assertEquals(Statistics.argmin(arr), 0);
    }

    @Test
    public void testSum() {
        assertEquals(Statistics.sum(arr), 17.0);
    }

    @Test
    public void testCumsum() {
        assertEquals(Statistics.cumsum(arr)[4], 17.0);
    }

    @Test
    public void testSummary() {
        System.out.println(Statistics.summary(arr));
    }

    @Test
    public void testMatrix() {
        double[][] arr = Statistics.matrix(1, 2, 2);
        for (double[] ar: arr) {
            System.out.println(Arrays.toString(ar));
        }

        arr = Statistics.matrixByCol(new double[]{1,2}, 3);
        for (double[] ar: arr) {
            System.out.println(Arrays.toString(ar));
        }

        arr = Statistics.matrixByRow(new double[]{1,2}, 3);
        for (double[] ar: arr) {
            System.out.println(Arrays.toString(ar));
        }

        arr = Statistics.transpose(arr);
        for (double[] ar: arr) {
            System.out.println(Arrays.toString(ar));
        }
        System.out.println(Arrays.toString(Statistics.colSums(arr)));
        System.out.println(Arrays.toString(Statistics.rowSums(arr)));
        System.out.println(Arrays.toString(Statistics.colMeans(arr)));
        System.out.println(Arrays.toString(Statistics.rowMeans(arr)));
    }

}