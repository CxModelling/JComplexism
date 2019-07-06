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

public class CumAttributeTest {
    private StSpABModel Model;
    private StSpY0 Y0;

    @Before
    public void setUp() throws Exception {
        Director Ctrl = new Director();

        setUpBN(Ctrl);
        setUpSS(Ctrl);
        setUpModel(Ctrl);

        Model = (StSpABModel) Ctrl.generateModel("Test", "abm", "pABC");

        Y0 = new StSpY0();
        Y0.append("A", 1000);


    }

    private void setUpBN(Director da) {
        BayesNet bn = da.createBayesNet("pABC");

        bn.appendLoci("sex ~ binom(1, 0.3)");
        bn.appendLoci("ToB ~ exp(0.2)");
        bn.appendLoci("ToC ~ exp(0.2)");
        bn.complete();
    }

    private void setUpSS(Director da) {
        CTMCBlueprint bp = (CTMCBlueprint) da.createStateSpace("ABC", "CTMC");
        bp.addState("A");
        bp.addState("B");
        bp.addState("C");
        bp.addTransition("ToB", "B");
        bp.addTransition("ToC", "C");
        bp.linkStateTransition("A", "ToB");
        bp.linkStateTransition("B", "ToC");
    }

    private void setUpModel(Director da) throws JSONException {
        StSpABMBlueprint mbp = (StSpABMBlueprint) da.createSimModel("abm", "StSpABM");
        mbp.setAgent("Ag", "agent", "ABC", new String[]{"sex"});

        mbp.addBehaviour("{'Name': 'bir', 'Type': 'Reincarnation', " +
                "'Args': {'s_death': 'C', 's_birth': 'A'}}");

        mbp.addBehaviour("{'Name': 'fe', 'Type': 'CumAttribute', " +
                "'Args': {'s_src': 'B', 'key': 'sex'}}");

        mbp.addBehaviour("{'Name': 'febir', 'Type': 'CumAttribute', " +
                "'Args': {'s_src': 'A', 'key': 'sex'}}");

        mbp.setObservations(new String[]{"A", "B", "C"}, new String[]{}, new String[]{"fe", "febir"});

    }

    @Test
    public void simulation() throws Exception {
        Simulator Simu = new Simulator(Model);
        //Simu.onLog("log/Ageing.txt");
        Simu.simulate(Y0, 2000, 2010, 1);
        Model.print();
    }
}