package org.twz.regression;

public class BooleanRegressor implements IRegressor {
    private String Name;
    private double Coefficient;

    public BooleanRegressor(String name, double coefficient) {
        Name = name;
        Coefficient = coefficient;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public double getEffect(double x) {
        return (x > 0)? Coefficient: 0;
    }
}
