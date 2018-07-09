package org.twz.cx.abmodel.modifier;

import org.twz.statespace.Transition;

/**
 *
 * Created by timewz on 30/09/17.
 */
public class LocRateModifier extends AbsModifier {
    private double Value;

    public LocRateModifier(String name, Transition target) {
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
    public boolean update(Object value) {
        double val = (double) value;
        if (val != Value & val >= 0) {
            Value = val;
            return true;
        } else {
            return false;
        }
    }

    public LocRateModifier clone() {
        LocRateModifier mod = new LocRateModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }
}
