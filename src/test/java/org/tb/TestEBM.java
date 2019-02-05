package org.tb;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.demographics.SexDemography;

public class TestEBM {

    private Director Da;
    private SexDemography DemoSex;

    @Before
    public void setUp() throws JSONException {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/tb/tb.txt");
        DemoSex = SexDemography.readCSV("src/test/resources/SimFM.csv", "Year",
                "PopF", "PopM", "DeathF", "DeathM",
                "BirthF", "BirthM", "MigrationF", "MigrationM");

        ODEEBMBlueprint bp = (ODEEBMBlueprint) Da.createSimModel("SIR", "ODEEBM");

        bp.setODE((t, y0, y1, parameters, attributes) -> {
            double beta = parameters.get("transmission_rate"), gamma = parameters.get("rec_rate");
            double n = y0[0] + y0[1] + y0[2];
            double foi = beta * y0[0] * y0[1] / n;
            y1[0] = - foi;
            y1[1] = foi - gamma * y0[1];
            y1[2] = gamma * y0[1];
        }, new String[]{"Sus", "Inf", "Rec"});

        bp.addMeasurementFunction((tab, ti, ys, pc, x) -> {
            double n = ys[0] + ys[1] + ys[2];
            tab.put("Prv", ys[1]/n);
            tab.put("N", n);
        });
        bp.setRequiredParameters(new String[]{"transmission_rate", "rec_rate"});
        bp.setObservations(new String[]{"Sus", "Inf", "Rec"});
    }

    @Test
    public void simulationDaBN() throws JSONException {
        run(Da.generateMCore("model", "SIR", "pCloseSIR"));
    }

    public void run(AbsSimModel model) throws JSONException {
        Simulator Simu = new Simulator(model);
        Simu.addLogPath("log/ODE.txt");
        EBMY0 y0 = new EBMY0();
        y0.append("{'y': 'Sus', 'n': 900}");
        y0.append("{'y': 'Inf', 'n': 100}");
        Simu.simulate(y0, 0, 10, 1);
        model.print();
    }
}
