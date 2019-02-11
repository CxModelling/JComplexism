package org.tb;


import org.json.JSONException;
import org.twz.cx.CxDataModel;
import org.twz.cx.Director;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.EquationBasedModel;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Gene;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.exception.TimeseriesException;
import org.twz.prob.Poisson;

import java.util.Map;

public class ReducedTB extends CxDataModel {

    private final double StartYear;
    private SexDemography Demo;
    private TimeSeries Noti;

    public ReducedTB(Director ctrl, SexDemography demo, double year0, TimeSeries noti) {
        super(ctrl, "pTB", "TB", year0, 2035, 0.5, "WarmUp", 300);
        Demo = demo;
        StartYear = year0;
        Noti = noti;
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
        double nf, nm, hf, hm, pf, pm;
        double li = 0;
        for (Double time : Noti.getTimes()) {
            try {
                pf = Demo.getPopulation(time, "Female");
                pm = Demo.getPopulation(time, "Male");
                nf = Noti.getDouble(time, "NumF");
                nm = Noti.getDouble(time, "NumM");
                hf = output.getDouble(time, "NotiF");
                hm = output.getDouble(time, "NotiM");
                li += (new Poisson(hf*pf)).logpdf(nf);
                li += (new Poisson(hm*pm)).logpdf(nm);
            } catch (TimeseriesException e) {
                e.printStackTrace();
            }

        }

        return li;
    }

    @Override
    public boolean hasExactLikelihood() {
        return false;
    }



    public static void setUpModel(Director da, SexDemography demo, double startYear, String dt_type) {

        String[] pars = new String[]{"beta", "partial_immune",
                "delay", "log_sr_t", "log_sr_m",
                "r_act", "r_ract", "r_rel", "r_cure", "r_treat", "r_slat", "r_die_tb"};

        String[] compartments = new String[]{
                "Sus", "LatFast", "LatSlow",
                "InfF", "InfM", "HosF", "HosM", "Rec"};

        ODEEBMBlueprint bp = (ODEEBMBlueprint) da.createSimModel("WarmUp", "ODEEBM");

        bp.setODE(new FnWarmUp(demo, startYear, dt_type), compartments);

        bp.setRequiredParameters(pars);
        bp.addExternalVariables("Demo", demo);
        bp.setDt(1);


        bp = (ODEEBMBlueprint) da.createSimModel("TB", "ODEEBM");

        bp.setODE(new FnTB(demo, startYear, dt_type), compartments);
        bp.addMeasurementFunction(new FnMeasure(demo, startYear, dt_type));
        bp.setRequiredParameters(pars);
        bp.addExternalVariables("Demo", demo);
        bp.setDt(1);
    }
}
