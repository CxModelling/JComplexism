package hgm.abmodel.modifier;

import dcore.Transition;

/**
 *
 * Created by timewz on 30/09/17.
 */
public class GloRateModifier extends AbsModifier {
    private double Value;

    public GloRateModifier(String name, Transition target) {
        super(name, target);
        Value = Double.POSITIVE_INFINITY;
    }

    @Override
    public double getValue() {
        return Value;
    }

    @Override
    public double modify(double tte) {
        if (Value == 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return tte/Value;
        }
    }

    @Override
    public boolean update(double value) {
        if (value != Value & value >= 0) {
            Value = value;
            return true;
        } else {
            return false;
        }
    }

    public GloRateModifier clone() {
        return this;
    }
}
