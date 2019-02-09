package org.tb;

import org.twz.cx.ebmodel.ODEFunction;
import org.twz.dag.Gene;
import org.twz.dataframe.Pair;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.exception.TimeseriesException;

import java.util.Map;

import static org.apache.commons.math3.stat.StatUtils.sum;

public class FnTB implements ODEFunction {
    final SexDemography Demo;
    final double StartYear;
    private final String FnT;

    public FnTB(SexDemography demo, double startYear, String fnT) {
        Demo = demo;
        StartYear = startYear;
        FnT = fnT;
    }

    @Override
    public void call(double t, double[] y0, double[] y1, Gene pars, Map<String, Object> attributes) {
        double sus = y0[0], flat = y0[1], slat = y0[2],
                inf_f = y0[3], inf_m = y0[4],
                hos_f = y0[5], hos_m = y0[6],
                rec = y0[7];

        double beta = expLU(pars.get("beta"), 1, 30);

        double n = sum(y0);
        double inf = inf_f + inf_m + hos_f + hos_m;
        double foi = beta * inf / n;
        double re_foi = foi * (1-pars.get("partial_immune"));

        double act = flat*pars.get("r_act") + slat*pars.get("r_ract") + rec*pars.get("r_rel");
        Pair<Double, Double> care_seeking = getCareSeekingRate(pars, t);
        double csf = care_seeking.getFirst(), csm = care_seeking.getSecond();

        y1[0] = - sus*foi;
        y1[1] = sus*foi + (slat+rec)*re_foi - flat*(pars.get("r_act")+pars.get("r_slat"));
        y1[2] = flat*pars.get("r_slat") - slat*(pars.get("r_ract")+re_foi);

        y1[3] = act*pars.get("kappa") - inf_f*(pars.get("r_cure")+csf);
        y1[4] = act*(1-pars.get("kappa")) - inf_m*(pars.get("r_cure")+csm);

        y1[5] = inf_f*csf - hos_f*(pars.get("r_cure")+pars.get("r_treat"));
        y1[6] = inf_m*csm - hos_m*(pars.get("r_cure")+pars.get("r_treat"));

        y1[7] = (inf_f+inf_m)*pars.get("r_cure")
                + (hos_f+hos_m)*(pars.get("r_cure")+pars.get("r_treat"))
                - rec*(pars.get("r_rel") + re_foi);

        double[] pdy = calculatePopDy(pars, y0, t);
        for (int i = 0; i < y1.length; i++) {
            y1[i] += pdy[i];
        }
    }

    private Pair<Double, Double> getCareSeekingRate(Gene pars, double t) {
        double sr0 = pars.get("delay") + pars.get("log_sr_t")*getDt(t);

        return new Pair<>(
                expLU(sr0, 0.5, 8.902*2),
                expLU(sr0 + pars.get("log_sr_m"), 0.5, 8.902*2)
        );
    }

    protected double[] calculatePopDy(Gene pars, double[] y, double t) {
        double[] pdy = new double[y.length];

        try {
            double bir = Demo.getBirthRate(t);
            double out = Demo.getDeathRate(t) - Demo.getMigration(t);
            double outF = Demo.getDeathRate(t, "Female") + pars.get("r_die_tb");
            double outM = Demo.getDeathRate(t, "Male") + pars.get("r_die_tb");

            double n = sum(y);
            pdy[0] = n*bir - y[0]*out;
            pdy[1] = - y[1]*out;
            pdy[2] = - y[2]*out;
            pdy[3] = - y[3]*outF;
            pdy[4] = - y[4]*outM;
            pdy[5] = - y[5]*outF;
            pdy[6] = - y[6]*outM;
            pdy[7] = - y[7]*out;
        } catch (TimeseriesException e) {
            e.printStackTrace();
        }
        return pdy;
    }

    protected double getDt(double t) {
        return fn_dt(t, FnT, StartYear);
    }

    protected double expLU(double log_rate, double lower, double upper) {
        double exp = Math.exp(log_rate);
        exp += lower;
        exp = Math.min(exp, upper);
        return exp;
    }

    static double fn_dt(double t, String fnT, double startYear) {
        switch (fnT) {
            case "l":
                return t - startYear;
            case "s":
                return Math.sqrt(t - startYear);
            default:
                return 0;
        }
    }
}
