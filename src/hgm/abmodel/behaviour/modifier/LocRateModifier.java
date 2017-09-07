package hgm.abmodel.behaviour.modifier;

import dcore.Transition;
import pcore.loci.Loci;

/**

 * Created by TimeWz on 2017/9/7.
 */
public class LocRateModifier extends AbsModifier {
    private double Value;

    public LocRateModifier(String name, Transition tar) {
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
        LocRateModifier mod = new LocRateModifier(getName(), getTarget());
        mod.Value = Value;
        return mod;
    }

}
