package org.twz.fit.genetic;

import org.twz.fit.ValueDomain;

import static org.twz.util.Statistics.bound;
import static org.twz.util.Statistics.mean;

public class BinaryMutator extends AbsMutator {
    private double Scale;

    public BinaryMutator(ValueDomain vd) {
        super(vd);
    }

    @Override
    public void setScale(double[] vs) {
        Scale = bound(mean(vs), 0, 1);;
    }

    @Override
    public double propose(double v) {
        if (Math.random() < Scale) {
            return 1-v;
        } else {
            return v;
        }

    }

    @Override
    public double calculateLogKernel(double v1, double v2) {
        return 0;
    }
}
