package org.tb;


import org.json.JSONException;
import org.twz.cx.CxFitter;
import org.twz.cx.Director;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.EquationBasedModel;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Gene;
import org.twz.dag.SimulationCore;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.exception.TimeseriesException;

import java.util.Map;

import static org.apache.commons.math3.stat.StatUtils.sum;

public class ReducedTB extends CxFitter {

    private double StartYear;
    private SexDemography Demo;

    public ReducedTB(SimulationCore sm, Director ctrl, SexDemography demo, double year0) {
        super(sm, ctrl, "TB", year0, 2035, 0.5, "WarmUp", 300);
        Demo = demo;
    }

    @Override
    protected IY0 sampleY0(Gene gene) {
        EBMY0 y0 = new EBMY0();
        double n = 0;
        try {
            n = Demo.getPopulation(StartYear);
        } catch (TimeseriesException e) {
            e.printStackTrace();
        }
        try {
            y0.append("{'y': 'Sus', 'n': "+ n*0.55 + "}");
            y0.append("{'y': 'LatFast', 'n': "+ n*0.01 + "}");
            y0.append("{'y': 'LatSlow', 'n': "+ n*0.214 + "}");
            y0.append("{'y': 'InfF', 'n': "+ n*0.001/2 + "}");
            y0.append("{'y': 'InfM', 'n': "+ n*0.001/2 + "}");
            y0.append("{'y': 'Rec', 'n': "+ n*0.225 + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return y0;
    }

    @Override
    protected boolean checkMidTerm(IY0 y0, Gene pars) {
        Map<String, Double> ys = ((EBMY0) y0).toMap();
        double act = ys.get("LatFast")*pars.get("r_act")
                + ys.get("LatSlow")*pars.get("r_ract")
                + ys.get("Rec")*pars.get("r_rel");
        return act * 1e5 > 10;
    }

    @Override
    protected IY0 transportY0(AbsSimModel model) {
        Map<String, Double> ys = ((EquationBasedModel) model).getEquations().getDictY();
        double scale = 1;
        try {
            scale *= Demo.getPopulation(StartYear);
            scale /= ys.values().stream().mapToDouble(e->e).sum();
        } catch (TimeseriesException e) {
            e.printStackTrace();
        }

        EBMY0 y0 = new EBMY0();
        for (Map.Entry<String, Double> ent : ys.entrySet()) {
            try {
                y0.append("{'y': '"+ ent.getKey() +"', 'n': "+ ent.getValue()*scale + "}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return y0;
    }

    @Override
    protected double calculateLogLikelihood(Gene gene, TimeSeries output) {
        return 0;
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
