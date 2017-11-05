package pcore.distribution;

/**
 *
 * Created by TimeWz on 2017/4/17.
 */
public interface IDistribution {
    String getName();
    double sample();
    double[] sample(int n);
    double logpdf(double rv);
    String getDataType();
    double getUpper();
    double getLower();
    double getMean();
    double getStd();
}
