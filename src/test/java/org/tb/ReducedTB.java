package org.tb;


import org.twz.cx.Director;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.dag.BayesNet;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.exception.TimeseriesException;

import static org.apache.commons.math3.stat.StatUtils.sum;

public class ReducedTB extends BayesianModel {

    public ReducedTB(BayesNet bn) {
        super(bn);
    }

    @Override
    public Gene samplePrior() {
        return null;
    }

    @Override
    public void evaluateLogLikelihood(Gene gene) {

    }

    @Override
    public boolean hasExactLikelihood() {
        return false;
    }


    public static void setUpModel(Director da, SexDemography demo, double startYear) {
        abstract class dT {
            abstract double eval(double t, double t0);
        }

        dT tfn = new dT() {
            @Override
            double eval(double t, double t0) {
                return t- t0;
            }
        };

        ODEEBMBlueprint bp = (ODEEBMBlueprint) da.createSimModel("TB", "ODEEBM");

        bp.setODE((t, y0, y1, pars, attributes) -> {

            double sus = y0[0], flat = y0[1], slat = y0[2], inf_f = y0[3], inf_m = y0[4],
                    hos_f = y0[5], hos_m = y0[6], rec = y0[7];

            double beta = expLU(pars.get("beta"), 1, 30);
            double sr_f = expLU(pars.get("delay") +
                    pars.get("log_sr_t")*0, 0.5, 8.902*2);
            double sr_m = expLU(pars.get("delay") +
                    pars.get("log_sr_t")*0 + pars.get("log_sr_m"), 0.5, 8.902*2);

            double n = sum(y0);
            double inf = inf_f + inf_m + hos_f + hos_m;
            double foi = beta * inf / n;
            double re_foi = foi * (1-pars.get("partial_immune"));

            double act = flat*pars.get("r_act") + slat*pars.get("r_ract") + rec*pars.get("r_rel");


            y1[0] = - sus*foi;
            y1[1] = sus*foi + (slat + rec)*re_foi - flat*(pars.get("r_act")+pars.get("r_slat"));

            y1[2] = flat*pars.get("r_slat") - slat*(pars.get("r_ract")+re_foi);

            y1[3] = act*pars.get("kappa") - inf_f*(pars.get("r_cure")+sr_f);
            y1[4] = act*(1-pars.get("kappa")) - inf_m*(pars.get("r_cure")+sr_m);


            y1[5] = inf_f*sr_f - hos_f*(pars.get("r_cure")+pars.get("r_treat"));
            y1[6] = inf_m*sr_m - hos_m*(pars.get("r_cure")+pars.get("r_treat"));

            y1[7] = (inf_f+inf_m)*pars.get("r_cure")
                    + (hos_f+hos_m)*(pars.get("r_cure")+pars.get("r_treat"))
                    - rec*(pars.get("r_rel") + re_foi);

            try {
                double bir = demo.getBirthRate(startYear);
                double out = demo.getDeathRate(startYear) - demo.getMigration(startYear);
                double outF = demo.getDeathRate(startYear, "Female") + pars.get("r_die_tb");
                double outM = demo.getDeathRate(startYear, "Male") + pars.get("r_die_tb");

                double[] pdy = new double[] {
                        n*bir - sus*out,
                        -flat*out,
                        -slat*out,
                        -inf_f*outF,
                        -inf_m*outM,
                        -hos_f*outF,
                        -hos_m*outM,
                        -rec*out
                };
                double sdy = sum(pdy)/n;

                for (int i = 0; i < y1.length; i++) {
                    y1[i] += pdy[i] - y0[i]*sdy;
                }

            } catch (TimeseriesException e) {
                e.printStackTrace();
            }



        }, new String[]{"Sus", "LatFast", "LatSlow", "InfF", "InfM", "HosF", "HosM", "Rec"});

        bp.addMeasurementFunction((tab, ti, ys, pars, x) -> {
            double n = sum(ys);
            double inf = ys[3] + ys[4] + ys[5] + ys[6];
            double act = ys[1]*pars.get("r_act") + ys[2]*pars.get("r_ract") + ys[7]*pars.get("r_rel");

            tab.put("Inc", act/n*1e5);
            tab.put("Prv", inf/n*1e5);
            tab.put("N", n/1e6);
        });
        bp.setRequiredParameters(new String[]{"beta", "partial_immune",
                            "delay", "log_sr_t", "log_sr_m",
                            "r_act", "r_ract", "r_rel", "r_cure", "r_treat", "r_slat", "r_die_tb"});
        //bp.setObservations(new String[]{"Sus", "Rec"});
        bp.addExternalVariables("Demo", demo);
        bp.setDt(0.5);

    }

    private static double expLU(double log_rate, double lower, double upper) {
        double exp = Math.exp(log_rate);
        exp += lower;
        exp = Math.min(exp, upper);
        return exp;
    }
}
