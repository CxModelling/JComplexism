package utils;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Arrays;

/**
 *
 * Created by TimeWz on 2016/7/13.
 */
public class StatisticsTest extends TestCase {

    double[] arr = {1, 2, 3, 5, 6};

    public void testSeq() throws Exception {
        System.out.println(Arrays.toString(Statistics.seq(0, 10, 4)));
        System.out.println(Arrays.toString(Statistics.seq(0, 10, 2)));
    }

    public void testMax() throws Exception {
        assertEquals(Statistics.max(arr), 6.0);
    }

    public void testArgmax() throws Exception {
        assertEquals(Statistics.argmax(arr), 4);
    }

    public void testMin() throws Exception {
        assertEquals(Statistics.min(arr), 1.0);
    }

    public void testArgmin() throws Exception {
        assertEquals(Statistics.argmin(arr), 0);
    }

    public void testSum() throws Exception {
        assertEquals(Statistics.sum(arr), 17.0);
    }

    public void testCumsum() throws Exception {
        assertEquals(Statistics.cumsum(arr)[4], 17.0);
    }

    public void testSummary() throws Exception {
        System.out.println(Statistics.summary(arr));
    }

    public void testMatrix() throws Exception {
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