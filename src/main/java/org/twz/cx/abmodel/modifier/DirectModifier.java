package org.twz.cx.abmodel.modifier;

import org.twz.statespace.Transition;

/**
 *
 * Created by timewz on 30/09/17.
 */
public class DirectModifier extends AbsModifier {
    private double Value;

    public DirectModifier(String name, Transition target) {
        super(name, target);
        Value = Double.POSITIVE_INFINITY;
    }

    @Override
    public double getValue() {
        return Value;
    }

    @Override
    public double modify(double tte) {
        return Value;
    }

    @Override
    public boolean update(Object value) {
        double val = (double) value;
        if (val != Value & val > 0) {
            Value = val;
            return true;
        } else {
            return false;
        }
    }

    public DirectModifier clone() {
        DirectModifier mod = new DirectModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }
}
