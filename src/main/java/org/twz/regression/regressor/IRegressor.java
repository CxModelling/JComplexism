package org.twz.regression.regressor;

public interface IRegressor {
    String getName();
    double getEffect(double x);
}
