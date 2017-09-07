package hgm.abmodel.behaviour.modifier;

import dcore.Transition;

/**

 * Created by TimeWz on 2017/9/7.
 */
public class NerfModifier extends AbsModifier {
    private boolean Value;

    public NerfModifier(String name, Transition tar) {
        super(name, tar);
        Value = false;
    }

    @Override
    public double getValue() {
        return Value? 1:0;
    }

    @Override
    public double modify(double tte) {
        if (Value) {
            return Double.POSITIVE_INFINITY;
        } else {
            return tte;
        }
    }

    @Override
    public boolean update(double val) {
        if (Value) {
            if (val <= 0) {
                Value = false;
                return true;
            }
        } else {
            if (val > 0) {
                Value = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public AbsModifier clone() {
        NerfModifier mod = new NerfModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }

}
