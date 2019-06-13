package org.twz.cx.multimodel;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.BayesNet;

public class TimeKeyTest {
    private Director Ctrl;
    private IY0 Y0s;

    @Before
    public void setUp() throws Exception {
        Ctrl = new Director();

        Ctrl.loadBayesNet("src/test/resources/script/pDzAB.txt");

        BayesNet bn = Ctrl.createBayesNet("temp");
        bn.appendLoci("year=0");
        Ctrl.joinBayesNets("pDzAB", "temp", "p");

        Ctrl.loadStateSpace("src/test/resources/script/DzAB.txt");

        StSpABMBlueprint BpA = (StSpABMBlueprint) Ctrl.createSimModel("abm", "StSpABM");
        BpA.setAgent("Ag", "agent", "DzAB");

        BpA.setObservations(new String[]{"ab", "Ab", "aB", "AB"}, new String[]{}, new String[]{});
        BpA.setSummariser((tab, model, ti) -> tab.put("t", model.getParameter("year")));
        BpA.setTimeKey("year");

        ODEEBMBlueprint BpE = (ODEEBMBlueprint) Ctrl.createSimModel("ebm", "ODEEBM");
        BpE.setODE((t, y0, y1, parameters, attributes) -> {
            y1[0] = 0;
            y1[1] = 0;
        }, new String[]{"B", "AB"});


        BpE.addMeasurementFunction((tab, ti, ys, pc, x) -> {
            tab.put("N", ys[0]+ys[1]);
            //tab.put("t", pc.getDouble("year"));
        });
        BpE.setObservations(new String[]{"B", "AB"});
        BpE.setSummariser((tab, model, ti) -> tab.put("t", model.getParameter("year")));
        BpE.setDt(0.5);


        ModelLayout layout = Ctrl.createModelLayout("hybrid");
        EBMY0 y0e = new EBMY0();
        layout.addEntry("eb", "ebm", y0e);

        StSpY0 y0a = new StSpY0();
        y0a.append("ab", 1000);
        layout.addEntry("ab", "abm", y0a);

        layout.setTimeKey("year");
        layout.setSummariser((tab, model, ti) -> tab.put("t", model.getParameter("year")));
        Y0s = Ctrl.generateModelY0("hybrid");
    }

    @Test
    public void runABM() throws Exception {
        StSpY0 y0 = new StSpY0();
        y0.append("ab", 1000);
        AbsSimModel model = Ctrl.generateModel("Test", "abm", "p");
        Simulator sim = new Simulator(model);
        sim.simulate(y0, 0, 10, 1);
        model.outputTS().print();
    }

    @Test
    public void runHybrid() throws Exception {
        AbsSimModel model = Ctrl.generateModel("Test", "hybrid", "p");
        Simulator sim = new Simulator(model);
        sim.onLog("log/Hybrid.txt");
        sim.simulate(Y0s, 0, 10, 1);
        model.outputTS().print();
    }
}