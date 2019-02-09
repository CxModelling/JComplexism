package org.tb;

import org.apache.commons.math3.stat.StatUtils;
import org.twz.cx.ebmodel.EBMMeasurement;
import org.twz.dag.Gene;
import org.twz.dataframe.Pair;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.exception.TimeseriesException;

import java.util.Map;

import static org.tb.FnTB.fn_dt;
import static org.twz.misc.Statistics.sum;


public class FnMeasure implements EBMMeasurement {
    private final SexDemography Demo;
    private final double StartYear;
    private final String FnT;

    public FnMeasure(SexDemography demo, double startYear, String fnT) {
        Demo = demo;
        StartYear = startYear;
        FnT = fnT;
    }

    @Override
    public void call(Map<String, Double> tab, double ti, double[] ys, Gene pars, Map<String, Object> x) {
        double sus = ys[0], flat = ys[1], slat = ys[2],
                inf_f = ys[3], inf_m = ys[4],
                hos_f = ys[5], hos_m = ys[6],
                rec = ys[7];

        double beta = expLU(pars.get("beta"), 1, 30);

        Pair<Double, Double> care_seeking = getCareSeekingRate(pars, ti);
        double csf = care_seeking.getFirst(), csm = care_seeking.getSecond();
        tab.put("beta", beta);
        tab.put("DelayF", 365/csf);
        tab.put("DelayM", 365/csm);

        try {
            double pf = Demo.getPopulation(ti, "Female")/Demo.getPopulation(ti);
            double pm = Demo.getPopulation(ti, "Male")/Demo.getPopulation(ti);

            double nf = (sus+flat+slat+rec)*pf + inf_f + hos_f;
            double nm = (sus+flat+slat+rec)*pm + inf_m + hos_m;
            tab.put("NotiF", inf_f*csf/nf);
            tab.put("NotiM", inf_m*csm/nm);
        } catch (TimeseriesException e) {
            e.printStackTrace();
        }

        double n = sum(ys);

        double act = flat*pars.get("r_act") + slat*pars.get("r_ract") + rec*pars.get("r_rel");

        tab.put("Lat", (flat+slat+rec)/n);
        tab.put("NewAct", flat*pars.get("r_act")/act);
        tab.put("Inc", act/n*1e5);
        tab.put("Prv", (inf_f+inf_m+hos_f+hos_m)/n*1e5);
        tab.put("N", n/1e6);
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

            double n = StatUtils.sum(y);
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
}
