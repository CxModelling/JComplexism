package hgm.abmodel.behaviour.modifier;

import dcore.Transition;

/**

 * Created by TimeWz on 2017/9/7.
 */
public class BuffModifier extends AbsModifier {
    private boolean Value;

    public BuffModifier(String name, Transition tar) {
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
            return 0;
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
        BuffModifier mod = new BuffModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }

}
