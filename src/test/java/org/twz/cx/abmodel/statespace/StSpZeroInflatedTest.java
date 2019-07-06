package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.BayesNet;
import org.twz.datafunction.NerfZeroInflatedSurvival;
import org.twz.datafunction.ZeroInflatedSurvival;
import org.twz.io.IO;
import org.twz.statespace.ctmc.CTMCBlueprint;

public class StSpZeroInflatedTest {
    private Director Ctrl;
    private StSpABMBlueprint Bp;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Ctrl = new Director();

        BayesNet bn = Ctrl.createBayesNet("Exp");
        bn.appendLoci("rate = 0.1");
        bn.appendLoci("ToB ~ exp(rate)");
        bn.complete();

        bn = Ctrl.createBayesNet("ZIExp");
        bn.appendLoci("rate = 0.1");
        bn.appendLoci("pr = 0.5");
        bn.appendLoci("ToB ~ ziexp(pr, rate)");
        bn.complete();

        bn = Ctrl.createBayesNet("Wei");
        bn.appendLoci("lambda = 0.1");
        bn.appendLoci("k = 0.99");
        bn.appendLoci("ToB ~ weibull(lambda, k)");
        bn.complete();

        bn = Ctrl.createBayesNet("ZIWei");
        bn.appendLoci("Sex ~ binom(1, 0.5)");
        bn.appendLoci("ToB ~ wei(Sex)");
        bn.complete();

        bn = Ctrl.createBayesNet("NZIWei");
        bn.appendLoci("year");
        bn.appendLoci("Sex ~ binom(1, 0.5)");
        bn.appendLoci("ToB ~ nwei(Sex, year)");
        bn.complete();

        CTMCBlueprint stsp = (CTMCBlueprint) Ctrl.createStateSpace("AB", "CTMC");
        stsp.addState("A");
        stsp.addState("B");
        stsp.addTransition("ToB", "B", "ToB");
        stsp.linkStateTransition("A", "ToB");


        Bp = (StSpABMBlueprint) Ctrl.createSimModel("AB", "StSpABM");
        Bp.setAgent("Ag", "agent", "AB", new String[]{"Sex"});

        Bp.setObservations(new String[]{"A", "B"}, new String[]{"ToB"}, new String[]{});
        Bp.setTimeKey("year");
        Y0 = new StSpY0();
        Y0.append("A", 1000);

        Ctrl.addDataFunction(new ZeroInflatedSurvival("wei",
                IO.loadJSON("src/test/resources/ZeroInflatedCox.json")));

        Ctrl.addDataFunction(new NerfZeroInflatedSurvival("nwei",
                IO.loadJSON("src/test/resources/ZeroInflatedCox.json"), 5.0));
    }

    @Test
    public void simulationExp() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("TestExp", "AB", "Exp");

        Simulator Simu = new Simulator(Model);

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }

    @Test
    public void simulateZIExp() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("TestExp", "AB", "ZIExp");

        Simulator Simu = new Simulator(Model);
        // Simu.onLog("log/Zi.txt");
        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }

    @Test
    public void simulateWeibull() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("TestWei", "AB", "Wei");

        Simulator Simu = new Simulator(Model);
        // Simu.onLog("log/Zi.txt");
        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }

    @Test
    public void simulateZiWeibull() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("TestZIWei", "AB", "ZIWei");

        Simulator Simu = new Simulator(Model);
        // Simu.onLog("log/Zi.txt");
        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }

    @Test
    public void simulateNerfZiWeibull() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("TestZIWei", "AB", "NZIWei");

        Simulator Simu = new Simulator(Model);
        // Simu.onLog("log/Zi.txt");
        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }
}
