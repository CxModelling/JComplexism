package utils;

import java.util.*;

/**
 *
 * Created by TimeWz on 2016/7/13.
 */

public class Statistics {
    public static double[] seq(double f, double t, double by) {
        int i = 0;
        double[] arr = new double[((int) Math.ceil((t-f)/by)) + 1];
        double k = f;

        while(k < t) {
            arr[i] = k;
            k += by;
            i ++;
        }
        arr[i] = t;
        return arr;
    }

    public static double[] rep(double k, int n) {
        double[] arr = new double[n];
        for (int i = 0; i < n; i++) {
            arr[i] = k;
        }
        return arr;
    }

    public static double[][] matrix(double k, int ni, int nj) {
        double[][] arr = new double[ni][nj];
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nj; j++) {
                arr[i][j] = k;
            }
        }
        return arr;
    }

    public static double[][] matrixByCol(double[] ks, int n) {
        double[][] arr = new double[ks.length][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < ks.length; j++) {
                arr[j][i] = ks[j];
            }
        }
        return arr;
    }


    public static double[][] matrixByRow(double[] ks, int n) {
        double[][] arr = new double[n][ks.length];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < ks.length; j++) {
                arr[i][j] = ks[j];
            }
        }
        return arr;
    }

    public static double[][] transpose(double[][] arr) {
        double[][] y = new double[arr[0].length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                y[j][i] = arr[i][j];
            }
        }
        return y;
    }

    public static double[] colSums(double[][] arr) {
        return rowSums(transpose(arr));
    }

    public static double[] rowSums(double[][] arr) {
        int nrow = arr.length;
        double[] ss = new double[nrow];

        for (int i = 0; i < nrow; i++) {
            ss[i] = sum(arr[i]);
        }
        return ss;
    }


    public static double[] colMeans(double[][] arr) {
        return rowMeans(transpose(arr));
    }

    public static double[] rowMeans(double[][] arr) {
        int nrow = arr.length;
        double[] ss = new double[nrow];

        for (int i = 0; i < nrow; i++) {
            ss[i] = mean(arr[i]);
        }
        return ss;
    }


    public static double max(double[] x) {
        double m = Double.NEGATIVE_INFINITY;
        for (double aX : x) {
            if (aX > m) {
                m = aX;
            }
        }
        return m;
    }


    public static int argmax(double[] x) {
        double m = Double.NEGATIVE_INFINITY;
        int ind = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] > m) {
                m = x[i];
                ind = i;
            }
        }
        return ind;
    }


    public static double min(double[] x) {
        double m = Double.POSITIVE_INFINITY;
        for (double aX : x) {
            if (aX < m) {
                m = aX;
            }
        }
        return m;
    }


    public static int argmin(double[] x) {
        double m = Double.POSITIVE_INFINITY;
        int ind = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] < m) {
                m = x[i];
                ind = i;
            }
        }
        return ind;
    }

    public static double sum(double[] x) {
        double m = 0;
        for (double a: x) {
            m += a;
        }
        return m;
    }

    public static double[] sum(List<int[]> xs) {
        double[] ms = new double[xs.get(0).length];
        for (int[] x: xs) {
            for (int i = 0; i < ms.length; i++) {
                ms[i] += x[i];
            }
        }
        return ms;
    }


    public static double[] cumsum(double[] x) {
        double[] y = new double[x.length];
        double k=x[0];
        y[0] = k;
        for (int i = 1; i < x.length; i++) {
            k += x[i];
            y[i] = k;
        }
        return y;
    }


    public static double sumsq(double[] x) {
        double m = 0;
        for (double a: x) {
            m += a * a;
        }
        return m;
    }

    public static double prod(double[] x) {
        double m = 1;
        for (double a: x) {
            m *= a;
        }
        return m;
    }

    public static double mean(double[] x) {
        return sum(x) / x.length;
    }

    public static double[] mean(List<int[]> xs) {
        int len = xs.size();
        double[] ms = sum(xs);
        for (int i = 0; i < ms.length; i++) {
            ms[i] /= len;
        }
        return ms;
    }

    public static double sd(double[] x) {
        return Math.sqrt(var(x));
    }

    public static double var(double[] x) {
        double mu = mean(x), v=0;
        for (double a: x) {
            v += Math.pow(a - mu, 2);
        }
        return v/(x.length-1);
    }

    public static double distance(int[] a, int[] b) {
        double x = 0, tmp;
        for (int i = 0; i < a.length; i++) {
            tmp = a[i] - b[i];
            x += tmp * tmp;
        }
        return Math.sqrt(x);
    }


    public static double quantile(double[] arr, double p) {
        double[] sorted = arr.clone();
        Arrays.sort(sorted);

        if (p <= 0) {
            return sorted[0];
        }else if (p >= 1) {
            return sorted[arr.length-1];
        }

        double rank, d, lower, upper;
        rank = p * (arr.length - 1);
        int lrank = (int) Math.floor(rank);
        d = rank - lrank;
        lower = sorted[lrank];
        upper = sorted[lrank+1];
        return lower + (upper - lower) * d;
    }

    public static double[] quantile(double[] arr, double[] ps) {
        double[] sorted = arr.clone(), qs = new double[ps.length];
        Arrays.sort(sorted);
        int i = 0, lrank;
        double rank, d, lower, upper;
        for (double p: ps) {
            if (p <= 0) {
                qs[i] = sorted[0];
            } else if (p >= 1) {
                qs[i] = sorted[arr.length-1];
            } else {
                rank = p * (arr.length - 1);
                lrank = (int) Math.floor(rank);
                d = rank - lrank;
                lower = sorted[lrank];
                upper = sorted[lrank+1];
                qs[i] = lower + (upper - lower) * d;
            }

            i ++;
        }
        return qs;
    }

    public static double[] CI(double[] arr) {
        return quantile(arr, new double[]{0.025, 0.975});
    }

    public static double IQR(double[] arr) {
        double[] qs = quantile(arr, new double[]{0.25, 0.75});
        return qs[1] = qs[0];
    }

    public static double range(double[] arr) {
        return max(arr) - min(arr);
    }

    public static double[] extend(double[] arr) {
        return new double[]{min(arr), max(arr)};
    }

    public static Map<String, Double> summary(double[] arr) {
        Map<String, Double> summ = new LinkedHashMap<>();
        summ.put("Mean", mean(arr));
        summ.put("Std", sd(arr));

        double[] qs =  quantile(arr, new double[]{0.025, 0.25, 0.5, 0.75, 0.975});
        summ.put("Min", qs[0]);
        summ.put("Q1", qs[1]);
        summ.put("Median", qs[2]);
        summ.put("Q3", qs[3]);
        summ.put("Max", qs[4]);

        return summ;
    }


    public static double[] add(double[] x, double a) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = x[i] + a;
        }
        return y;
    }

    public static double[] add(double[] x, double[] a) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = x[i] + a[i];
        }
        return y;
    }

    public static double lse(double[] x) {
        double m = max(x);
        if (Double.isInfinite(m)) {
            return Double.NEGATIVE_INFINITY;
        }

        return Math.log(sum(exp(add(x, -m)))) + m;
    }

    public static double[] mul(double[] x, double a) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = x[i] * a;
        }
        return y;
    }

    public static double[] mul(double[] x, double[] a) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = x[i] * a[i];
        }
        return y;
    }

    public static double[] logNormalise(double[] x) {
        return add(x, - lse(x));
    }

    public static double[] exp(double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = Math.exp(x[i]);
        }
        return y;
    }

    public static double[] log(double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = Math.log(x[i]);
        }
        return y;
    }


    public static int[] count(Integer[] data, int n) {
        int[] ct = new int[n];
        for (Integer x : data) {
            ct[x] += 1;
        }
        return ct;
    }

    public static double[] freq(Integer[] data, int n) {
        int[] ct = count(data, n);
        double[] fr = new double[n];
        for (int i = 0; i < n; i++) {
            fr[i] = ct[i]/(double) data.length;
        }
        return fr;
    }

    public static double rate2prob(double rate) {
        return 1 - Math.exp(-rate);
    }

    public static double prob2rate(double prob) {
        return - Math.log(1 - prob);
    }

    public static double[] jitter(double[] xs) {
        double r = range(xs), amount = r/25;
        if (Double.isInfinite(r)) return xs;
        if (Math.floor(xs[0]) == xs[0]) return xs;
        double[] ys = new double[xs.length];
        for (int i = 0; i < xs.length; i++) {
            ys[i] = xs[i] + (Math.random()-0.5)*amount;
        }
        return ys;

    }

    public double rnorm() {
        double u, v, x, y, q;
        do {
            u = Math.random();
            v = 1.7156 * (Math.random() - 0.5);
            x = u - 0.449871;
            y = Math.abs(v) + 0.386595;
            q = x * x + y * (0.19600 * y - 0.25472 * x);
        } while (q > 0.27597 && (q > 0.27846 || v * v > -4 * Math.log(u) * u * u));
        return v / u;
    }
}
