package org.twz.cx;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.BayesNet;
import org.twz.dag.Chromosome;
import org.twz.dag.Parameters;
import org.twz.dataframe.Pair;
import org.twz.dataframe.TimeSeries;
import org.twz.fit.AbsFitter;
import org.twz.fit.BayesianFitter;
import org.twz.fit.MCMC;
import org.twz.fit.SampImpResamp;
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DataModelTest {

    private Director Ctrl;
    private DataModelSIR DM;

    @Before
    public void setUp() throws Exception {
        Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Ctrl.loadBayesNet("src/test/resources/script/DataSIR.txt");
        TimeSeries data = TimeSeries.readCSV("src/test/resources/ToFitSIR.csv", "Time");

        DataModelSIR.setUpModel(Ctrl);

        DM = new DataModelSIR(Ctrl, "pCloseSIR", "SIR", 0, 7, 1);
        DM.setData(data, "DataSIR");
    }

    @Test
    public void simulate() throws Exception {
        Pair<Chromosome, TimeSeries> p_ts = DM.testRun();
        p_ts.getSecond().print();

        System.out.println(p_ts.getFirst().toString());
    }

    @Test
    public void fit() throws Exception {
        BayesianFitter fitter = new MCMC(1000, 1000, 3, DM.getMovableNodes());
        DM.fit(fitter);
        fitter.summariseParameters(DM.getResults());
        // DM.saveMementosJSON("src/test/resources/FittedSIR.json", "Posterior");
    }
}