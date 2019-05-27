package org.twz.cx;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.dag.Chromosome;
import org.twz.dataframe.Pair;
import org.twz.dataframe.TimeSeries;
import org.twz.io.FnJSON;
import org.twz.io.IO;

import static org.junit.Assert.*;

public class ExperimentTest {
    private Director Ctrl;
    private ExperimentSIR Exp;

    @Before
    public void setUp() throws Exception {
        Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Ctrl.loadBayesNet("src/test/resources/script/DataSIR.txt");
        TimeSeries data = TimeSeries.readCSV("src/test/resources/ToFitSIR.csv", "Time");

        // DataModelSIR.setUpModel(Ctrl);

        Exp = new ExperimentSIR(Ctrl, "pCloseSIR", "SIR", 0, 10, 1);
        Exp.loadPosterior(IO.loadJSONArray("src/test/resources/FittedSIR.json"));
    }

    @Test
    public void testRun() throws JSONException {
        Pair<Chromosome, TimeSeries> p_ts = Exp.testRun();
        p_ts.getSecond().print();

        System.out.println(p_ts.getFirst().toJSON().toString(4));
    }

    @Test
    public void run() {
        Exp.start();
        // Exp.saveResultsByVariable("E://", "S_", ".csv");
    }
}