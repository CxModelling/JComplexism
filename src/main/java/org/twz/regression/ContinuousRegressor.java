package org.twz.regression;

public class ContinuousRegressor implements IRegressor {
    private String Name;
    private double Coefficient;

    public ContinuousRegressor(String name, double beta) {
        Name = name;
        Coefficient = beta;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public double getEffect(double x) {
        return x * Coefficient;
    }
}
