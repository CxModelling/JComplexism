package org.tb;

import org.twz.dag.Chromosome;
import org.twz.dataframe.demographics.SexDemography;

import static org.apache.commons.math3.stat.StatUtils.sum;


public class FnWarmUp extends FnTB {
    public FnWarmUp(SexDemography demo, double startYear, String fnT) {
        super(demo, startYear, "l");
    }

    @Override
    protected double[] calculatePopDy(Chromosome pars, double[] y, double t) {
        double[] pdy = super.calculatePopDy(pars, y, StartYear);
        double sdy = sum(pdy)/sum(y);

        for (int i = 0; i < y.length; i++) {
            pdy[i] -= y[i]*sdy;
        }
        return pdy;
    }


    @Override
    protected double getDt(double t) {
        return 0;
    }

}
