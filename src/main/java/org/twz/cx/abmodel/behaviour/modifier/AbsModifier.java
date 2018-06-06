package org.twz.cx.abmodel.behaviour.modifier;

import org.twz.statespace.Transition;

/**
 *
 * Created by TimeWz on 2017/9/7.
 */
public abstract class AbsModifier implements Cloneable {
    private final String Name;
    private final Transition Target;

    public AbsModifier(String name, Transition tar) {
        Name = name;
        Target = tar;
    }

    public String getName() {
        return Name;
    }

    public Transition getTarget() {
        return Target;
    }


    public abstract double getValue();
    public abstract double modify(double tte);
    public abstract boolean update(double val);
    public abstract AbsModifier clone();

}
