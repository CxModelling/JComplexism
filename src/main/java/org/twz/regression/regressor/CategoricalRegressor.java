package org.twz.regression.regressor;

public class CategoricalRegressor implements IRegressor {
    private String Name;
    private String[] Labels;
    private double[] Coefficients;
    private int Reference;

    public CategoricalRegressor(String name, double[] beta, String[] labels, String ref) {
        Name = name;
        Coefficients = beta;
        Labels = labels;

        Reference = -1;
        for (int i = 0; i < labels.length; i++) {
            if (Labels[i].equals(ref)) {
                Reference = i;
                break;
            }
        }
        assert Reference > 0;
        if (Coefficients[Reference] != 0) {
            double v = Coefficients[Reference];
            for (int i = 0; i < labels.length; i++) {
                Coefficients[i] -= v;
            }
        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public double getEffect(double x) {
        int i = (int) x;
        return Coefficients[i];
    }
}
