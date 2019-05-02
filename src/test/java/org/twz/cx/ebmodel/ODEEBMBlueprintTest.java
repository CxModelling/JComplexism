package org.twz.cx.ebmodel;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;

import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.Parameters;


/**
 *
 * Created by TimeWz on 22/08/2018.
 */
public class ODEEBMBlueprintTest {
    private Director Da;

    @Before
    public void setUp() throws JSONException {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pCloseSIR.txt");


        ODEEBMBlueprint bp = (ODEEBMBlueprint) Da.createSimModel("SIR", "ODEEBM");

        bp.setODE((t, y0, y1, parameters, attributes) -> {
            double beta = parameters.getDouble("transmission_rate"), gamma = parameters.getDouble("rec_rate");
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
    public void simulationPcDc() throws Exception {
        Parameters PC = Da.getBayesNet("pCloseSIR")
                .toParameterModel(Da.getSimModel("SIR").getParameterHierarchy(Da))
                .generate("Test");

        run(Da.generateModel("model", "SIR", PC));
    }

    @Test
    public void simulationDaBN() throws Exception {
        run(Da.generateModel("model", "SIR", "pCloseSIR"));
    }

    public void run(AbsSimModel model) throws Exception {
        Simulator Simu = new Simulator(model);
        Simu.onLog("log/ODE.txt");
        EBMY0 y0 = new EBMY0();
        y0.append("{'y': 'Sus', 'n': 900}");
        y0.append("{'y': 'Inf', 'n': 100}");
        Simu.simulate(y0, 4, 8, 0.25);
        model.print();
    }
}