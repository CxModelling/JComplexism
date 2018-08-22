package org.twz.cx.ebmodel;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;

import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;


/**
 *
 * Created by TimeWz on 22/08/2018.
 */
public class ODEEBMBlueprintTest {
    private Director Da;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pCloseSIR.txt");


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
    public void simulationPcDc() {
        ParameterCore PC = Da.getBayesNet("pCloseSIR")
                .toSimulationCore(Da.getSimModel("SIR").getParameterHierarchy(Da), true)
                .generate("Test");

        run(Da.generateMCore("model", "SIR", PC));
    }

    @Test
    public void simulationDaBN() {
        run(Da.generateMCore("model", "SIR", "pCloseSIR"));
    }

    public void run(AbsSimModel model) {
        Simulator Simu = new Simulator(model);
        Simu.addLogPath("log/ODE.txt");
        EBMY0 y0 = new EBMY0();
        y0.append("{'y': 'Sus', 'n': 900}");
        y0.append("{'y': 'Inf', 'n': 100}");
        Simu.simulate(y0, 0, 10, 1);
        model.print();
    }
}