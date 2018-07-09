package org.twz.cx.abmodel.modifier;

import org.twz.statespace.Transition;

/**
 *
 * Created by timewz on 30/09/17.
 */
public class BuffRateModifier extends AbsModifier {
    private boolean Value;

    public BuffRateModifier(String name, Transition target) {
        super(name, target);
        Value = false;
    }

    @Override
    public double getValue() {
        return Value? 1.0:0.0;
    }

    @Override
    public double modify(double tte) {
        if (Value) {
            return 0;
        } else {
            return tte;
        }
    }

    @Override
    public boolean update(Object value) {
        double val = (double) value;
        if (val > 0 ^ Value) {
            Value = !Value;
            return true;
        } else {
            return false;
        }
    }

    public BuffRateModifier clone() {
        BuffRateModifier mod = new BuffRateModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }
}
