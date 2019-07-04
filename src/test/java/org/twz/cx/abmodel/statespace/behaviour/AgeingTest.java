package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.BayesNet;
import org.twz.datafunction.PrAgeByYearSex;
import org.twz.io.IO;
import org.twz.statespace.ctmc.CTMCBlueprint;

public class AgeingTest {
    private StSpABModel Model;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Director Ctrl = new Director();

        Ctrl.addDataFunction(new PrAgeByYearSex("pr",
                IO.loadJSON("src/test/resources/N_ys.json")));

        setUpBN(Ctrl);
        setUpSS(Ctrl);
        setUpModel(Ctrl);

        //AbsScheduler.DefaultScheduler = "ArrayList";
        Model = (StSpABModel) Ctrl.generateModel("Test", "abm", "pAgeing");

        Y0 = new StSpY0();
        Y0.append("Act", 1000);


    }

    private void setUpBN(Director da) {
        BayesNet bn = da.createBayesNet("pAgeing");

        bn.appendLoci("year = 2000");
        bn.appendLoci("sex ~ binom(1, 0.5)");
        bn.appendLoci("age ~ pr(year, sex)");
        bn.appendLoci("delay ~ exp(0.05)");
        bn.complete();
    }

    private void setUpSS(Director da) {
        CTMCBlueprint bp = (CTMCBlueprint) da.createStateSpace("Ageing", "CTMC");
        bp.addState("Act");
        bp.addState("Hos");
        bp.addTransition("Care", "Hos", "delay");
        bp.linkStateTransition("Act", "Care");
    }

    private void setUpModel(Director da) throws JSONException {
        StSpABMBlueprint mbp = (StSpABMBlueprint) da.createSimModel("abm", "StSpABM");
        mbp.setAgent("Ag", "agent", "Ageing", new String[]{"sex", "age"});

        mbp.addBehaviour("{'Name': 'A', 'Type': 'Ageing', 'Args': {'key': 'age'}}");

        mbp.setObservations(new String[]{"Act"}, new String[]{"Care"}, new String[]{"A"});

    }

    @Test
    public void simulation() throws Exception {
        Simulator Simu = new Simulator(Model);
        Simu.onLog("log/Ageing.txt");
        Simu.simulate(Y0, 2000, 2010, 1);
        Model.print();
    }
}