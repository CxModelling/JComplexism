package org.twz.cx.abmodel.behaviour.modifier;

import org.twz.statespace.Transition;

/**

 * Created by TimeWz on 2017/9/7.
 */
public class GloRateModifier extends AbsModifier {
    private double Value;

    public GloRateModifier(String name, Transition tar) {
        super(name, tar);
        Value = 1;
    }

    @Override
    public double getValue() {
        return Value;
    }

    @Override
    public double modify(double tte) {
        if (Value > 0) {
            return tte/Value;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public boolean update(double val) {
        if (val != Value & val > 0) {
            Value = val;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public AbsModifier clone() {
        return this;
    }

}
