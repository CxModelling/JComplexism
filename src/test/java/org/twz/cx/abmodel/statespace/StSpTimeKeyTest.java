package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.BayesNet;
import org.twz.statespace.ctmc.CTMCBlueprint;

public class StSpTimeKeyTest {
    private Director Ctrl;
    private StSpABMBlueprint Bp;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Ctrl = new Director();

        BayesNet bn = Ctrl.createBayesNet("pA");
        bn.appendLoci("year = 0");
        bn.appendLoci("sex~binom(1, 0.5)");
        bn.appendLoci("dA ~ exp(1)");
        bn.appendLoci("da1 ~ exp(1)");
        bn.appendLoci("da2 ~ exp(sex)");
        bn.appendLoci("da = if(year < 5, da1, da2)");

        CTMCBlueprint ssbp = (CTMCBlueprint) Ctrl.createStateSpace("DzA", "CTMC");
        ssbp.addState("A");
        ssbp.addState("a");
        ssbp.addTransition("dA", "a");
        ssbp.addTransition("da", "A");
        ssbp.linkStateTransition("A", "dA");
        ssbp.linkStateTransition("a", "da");

        Bp = (StSpABMBlueprint) Ctrl.createSimModel("DzA", "StSpABM");
        Bp.setAgent("Ag", "agent", "DzA");
        Bp.setTimeKey("year");
        Bp.setObservations(new String[]{"A", "a"}, new String[]{"dA", "da"}, new String[]{});
        Bp.setPars(new String[0], new String[]{"da1", "da2"});

        Y0 = new StSpY0();
        Y0.append("A", 500);
        Y0.append("a", 500);
    }


    @Test
    public void simulationDaBN() throws Exception {
        AbsSimModel model = Ctrl.generateModel("test", "DzA", "pA");

        Simulator Simu = new Simulator(model);

        Simu.simulate(Y0, 0, 10, 1);
        model.getObserver().print();
    }
}