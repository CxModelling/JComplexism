package org.tb;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.CxFitter;
import org.twz.cx.Director;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.SimulationCore;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.exception.TimeseriesException;

public class TestEBM {

    private Director Da;
    private SexDemography DemoSex;
    private CxFitter BM;

    @Before
    public void setUp() throws JSONException {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/tb/tb.txt");
        DemoSex = SexDemography.readCSV("src/test/resources/SimFM.csv", "Year",
                "PopF", "PopM", "DeathF", "DeathM",
                "BirthF", "BirthM", "MigrationF", "MigrationM");

        ReducedTB.setUpModel(Da, DemoSex, 1990);

        SimulationCore SC = Da.getBayesNet("pTB")
                .toSimulationCoreNoOut(Da.getParameterHierarchy("TB"), true);
        BM = new ReducedTB(SC, Da, DemoSex, 1990);

    }

    @Test
    public void simulationDaBN() throws NullPointerException, JSONException, TimeseriesException {
        run(Da.generateMCore("model", "TB", "pTB"));
    }

    public void run(AbsSimModel model) throws JSONException, TimeseriesException {
        //((ODEquations)((EquationBasedModel) model).getEquations())
        //        .setIntegrator(new EulerIntegrator(3));
        Simulator Simu = new Simulator(model);
        Simu.onLog("log/ODE.txt");
        EBMY0 y0 = new EBMY0();
        double n = DemoSex.getPopulation(1990);
        y0.append("{'y': 'Sus', 'n': "+ n*0.55 + "}");
        y0.append("{'y': 'LatFast', 'n': "+ n*0.01 + "}");
        y0.append("{'y': 'LatSlow', 'n': "+ n*0.214 + "}");
        y0.append("{'y': 'InfF', 'n': "+ n*0.001/2 + "}");
        y0.append("{'y': 'InfM', 'n': "+ n*0.001/2 + "}");
        y0.append("{'y': 'Rec', 'n': "+ n*0.225 + "}");

        Simu.simulate(y0, 0, 300, 1);
        model.getObserver().getObservations().print("4");
    }
}
