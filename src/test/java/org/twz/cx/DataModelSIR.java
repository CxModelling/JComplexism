package org.twz.cx;

import org.json.JSONException;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.dag.BayesNet;
import org.twz.dag.Chromosome;
import org.twz.dataframe.TimeSeries;

import java.util.Map;

public class DataModelSIR extends DataModel {

    private TimeSeries Data;
    private BayesNet DataDAG;


    public DataModelSIR(Director ctrl, String bn, String simModel, double t0, double t1, double dt) {
        super(ctrl, bn, simModel, t0, t1, dt);
    }

    void setData(TimeSeries data, String bn) {
        Data = data;
        DataDAG = Ctrl.getBayesNet(bn);
    }

    @Override
    protected IY0 sampleY0(Chromosome chromosome) {
        EBMY0 y0 = new EBMY0();
        try {
            y0.append("{'y': 'Sus', 'n': 990}");
            y0.append("{'y': 'Inf', 'n': 10}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return y0;
    }

    @Override
    protected boolean checkMidTerm(IY0 y0, Chromosome chromosome) {
        return true;
    }

    @Override
    protected IY0 transportY0(AbsSimModel model) {
        return null;
    }

    @Override
    protected double calculateLogLikelihood(Chromosome chromosome, TimeSeries output) {
        Map<Double, Map<String, Double>> data = TimeSeries.combineAllNumbers(output, Data);

        Chromosome datum;
        double li = 0;
        for (Map.Entry<Double, Map<String, Double>> ent : data.entrySet()) {
            datum = DataDAG.sample(ent.getValue());
            li += datum.getLogPriorProb();
        }

        return li;
    }

    static void setUpModel(Director ctrl) {
        ODEEBMBlueprint bp = (ODEEBMBlueprint) ctrl.createSimModel("SIR", "ODEEBM");

        bp.setODE((t, y0, y1, parameters, attributes) -> {
            double beta = parameters.getDouble("beta"), gamma = parameters.getDouble("gamma");
            double n = y0[0] + y0[1] + y0[2];
            double foi = beta * y0[0] * y0[1] / n;
            y1[0] = - foi;
            y1[1] = foi - gamma * y0[1];
            y1[2] = gamma * y0[1];
        }, new String[]{"Sus", "Inf", "Rec"});

        bp.addMeasurementFunction((tab, ti, ys, parameters, x) -> {
            double n = ys[0] + ys[1] + ys[2];
            tab.put("Prv", ys[1]/n);
            double beta = parameters.getDouble("beta");

            tab.put("inc_rate", (double) Math.round(beta * ys[0] * ys[1] / n));
            tab.put("n", n);
        });
        bp.setRequiredParameters(new String[]{"beta", "gamma"});
        bp.setObservations(new String[]{"Sus", "Inf", "Rec"});
    }

}
