package org.twz.cx.multimodel;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.ebmodel.AbsEquations;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.EquationBasedModel;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.cx.mcore.communicator.*;
import org.twz.dataframe.Pair;
import org.twz.datafunction.AbsDataFunction;
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.IWalkable;
import org.twz.prob.Poisson;

public class ModelAgentExportTest {
    private Director Ctrl;
    private IY0 Y0s;

    @Before
    public void setUp() throws Exception {
        Ctrl = new Director();

        Ctrl.loadBayesNet("src/test/resources/script/pDzAB.txt");
        Ctrl.loadStateSpace("src/test/resources/script/DzAB.txt");

        StSpABMBlueprint BpA = (StSpABMBlueprint) Ctrl.createSimModel("abm", "StSpABM");
        BpA.setAgent("Ag", "agent", "DzAB");

        BpA.addBehaviour("{'Name': 'ToAB', 'Type': 'Cohort', 'Args': {'s_death': 'AB'}}");
        BpA.addBehaviour("{'Name': 'ToB', 'Type': 'Cohort', 'Args': {'s_death': 'B'}}");



        BpA.setObservations(new String[]{"ab", "Ab", "aB", "AB"}, new String[]{}, new String[]{"ToAB", "ToB"});


        ODEEBMBlueprint BpE = (ODEEBMBlueprint) Ctrl.createSimModel("ebm", "ODEEBM");
        BpE.setODE((t, y0, y1, parameters, attributes) -> {
            y1[0] = 0;
            y1[1] = 0;
        }, new String[]{"B", "AB"});


        BpE.addMeasurementFunction((tab, ti, ys, pc, x) -> {
            tab.put("N", ys[0]+ys[1]);
        });
        BpE.setObservations(new String[]{"B", "AB"});
        BpE.setDt(0.5);


        ModelLayout layout = Ctrl.createModelLayout("hybrid");
        EBMY0 y0e = new EBMY0();
        layout.addEntry("eb", "ebm", y0e);

        StSpY0 y0a = new StSpY0();
        y0a.append(1000, "ab");
        layout.addEntry("ab", "abm", y0a);


        layout.addInteraction("eb",
                new WhoStartWithChecker("ToAB", "remove"),
                new AddOneResponse("AB"));

        layout.addInteraction("eb",
                new WhoStartWithChecker("ToB", "remove"),
                new AddOneResponse("B"));


        Y0s = Ctrl.generateModelY0("hybrid");
    }

    @Test
    public void generate() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("Hybrid", "Hybrid", "pHySIR");
        Simulator Sim = new Simulator(Model);
        Sim.onLog("log/Hybrid.txt");

        Sim.simulate(Y0s, 5, 30, 0.25);
        Model.getObserver().getObservations().print();
    }

    @Test
    public void runABM() throws Exception {
        StSpY0 y0 = new StSpY0();
        y0.append(1000, "ab");
        AbsSimModel model = Ctrl.generateModel("Test", "abm", "pDzAB");
        Simulator sim = new Simulator(model);
        sim.simulate(y0, 0, 10, 1);
        model.outputTS().print();
    }

    @Test
    public void runHybrid() throws Exception {
        AbsSimModel model = Ctrl.generateModel("Test", "hybrid", "pDzAB");
        Simulator sim = new Simulator(model);
        sim.onLog("log/Hybrid.txt");
        sim.simulate(Y0s, 0, 10, 1);
        model.outputTS().print();
    }
}