package org.twz.fit.genetic;

import org.twz.fit.ValueDomain;

public abstract class AbsMutator {
    protected final String Name;
    protected final double Lower, Upper;
    protected double Scale;


    protected AbsMutator(ValueDomain vd) {
        this(vd.Name, vd.Lower, vd.Upper);
    }

    protected AbsMutator(String name, double lower, double upper) {
        Name = name;
        Lower = lower;
        Upper = upper;
        Scale = 0;
    }

    public abstract void setScale(double[] vs);

    public abstract double propose(double v);

    public abstract double calculateLogKernel(double v1, double v2);
}
