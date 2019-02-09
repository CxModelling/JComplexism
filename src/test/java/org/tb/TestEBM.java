package org.tb;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import org.twz.cx.Director;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.mcore.IY0;
import org.twz.dag.ParameterCore;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.demographics.SexDemography;
import org.twz.fit.ABC;



import java.util.Map;

public class TestEBM {

    private ReducedTB BM;

    @Before
    public void setUp() throws JSONException {
        Director da = new Director();
        da.loadBayesNet("src/test/resources/tb/tb.txt");
        SexDemography demoSex = SexDemography.readCSV("src/test/resources/SimFM.csv", "Year",
                "PopF", "PopM", "DeathF", "DeathM",
                "BirthF", "BirthM", "MigrationF", "MigrationM");

        ReducedTB.setUpModel(da, demoSex, 1990, "l");

        TimeSeries Noti = TimeSeries.readCSV("src/test/resources/tb/NotiYear.csv", "Year");
        BM = new ReducedTB(da, demoSex,1990, Noti);

    }

    @Test
    public void fit() {
        ABC alg = new ABC(BM);
        alg.onLog();
        alg.fit(100);
        alg.summarisePosterior();

    }

    @Test
    public void simulationDaBN() throws NullPointerException {
        ParameterCore pc = (ParameterCore) BM.samplePrior();
        System.out.println(pc);
        IY0 y0 = BM.sampleY0(pc);

        for(Map.Entry<String, Double> ent : ((EBMY0) y0).toMap().entrySet()) {
            System.out.println(ent.getKey() + ": \t"+ ent.getValue());
        }
        System.out.println();

        y0 = BM.warmUp(pc);

        for(Map.Entry<String, Double> ent : ((EBMY0) y0).toMap().entrySet()) {
            System.out.println(ent.getKey() + ": \t"+ ent.getValue());
        }
        System.out.println();

        TimeSeries ts = BM.simulate(pc, y0);
        ts.print();
        ts.toCSV("tb/Ts.csv");
        System.out.println(pc.getDeepLogPrior());

        System.out.println(BM.calculateLogLikelihood(pc, ts));
    }

}
