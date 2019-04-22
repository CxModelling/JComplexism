package org.twz.datafunction;

import org.junit.Before;
import org.twz.cx.Director;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.io.IO;

import static org.junit.Assert.*;

public class RateAgeByYearSexODETest {
    private Director Da;
    private RateAgeByYearSex RateAge;

    @Before
    public void setUp() throws Exception {
        RateAge = new RateAgeByYearSex("YAS",
                IO.loadJSON("src/test/resources/D_ys.json"));


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
}