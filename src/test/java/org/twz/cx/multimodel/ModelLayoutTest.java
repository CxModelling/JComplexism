package org.twz.cx.multimodel;

import org.json.JSONException;
import org.json.JSONObject;
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
import org.twz.cx.mcore.communicator.IsChecker;
import org.twz.cx.mcore.communicator.StartWithChecker;
import org.twz.cx.mcore.communicator.ValueImpulseResponse;
import org.twz.dataframe.Pair;

public class ModelLayoutTest {
    private Director Ctrl;
    private IY0 Y0s;

    @Before
    public void setUp() throws Exception {
        Ctrl = new Director();

        Ctrl.loadBayesNet("src/test/resources/script/pHybridSIR.txt");
        Ctrl.loadStateSpace("src/test/resources/script/HybridSIR.txt");

        StSpABMBlueprint BpA = (StSpABMBlueprint) Ctrl.createSimModel("abm", "StSpABM");
        BpA.setAgent("Ag", "agent", "HySIR");

        BpA.addBehaviour("{'Name': 'Recovery', 'Type': 'Cohort', 'Args': {'s_death': 'Rec'}}");
        BpA.addBehaviour("{'Name': 'StInf', 'Type': 'StateTrack', 'Args': {'s_src': 'Inf'}}");
        BpA.addBehaviour("{'Name': 'InfIn', 'Type': 'AgentImport', 'Args': {'s_birth': 'Inf'}}");
        BpA.setObservations(new String[]{"Inf"}, new String[]{"Recov"}, new String[]{"InfIn", "StInf"});


        ODEEBMBlueprint BpE = (ODEEBMBlueprint) Ctrl.createSimModel("ebm", "ODEEBM");
        BpE.setODE((t, y0, y1, parameters, attributes) -> {
            y1[0] = 0;
            y1[1] = 0;
        }, new String[]{"S", "R"});

        BpE.appendExternalVariable("Inf", 0);
        BpE.addMeasurementFunction((tab, ti, ys, pc, x) -> {
            double inf = 0;
            try {
                inf = x.getDouble("Inf");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double n = ys[0] + ys[1] + inf;
            tab.put("Prv", inf/n);
            tab.put("N", n);
        });
        BpE.setObservations(new String[]{"S", "R"});


        ModelLayout layout = Ctrl.createModelLayout("HybridSIR");
        EBMY0 y0e = new EBMY0();
        y0e.append("{'y': 'S', 'n': 950}");
        layout.addEntry("SR", "ebm", y0e);

        StSpY0 y0a = new StSpY0();
        y0a.append(50, "Inf");
        layout.addEntry("I", "abm", y0a);

        layout.addInteraction("SR",
                new StartWithChecker("update value"),
                new ValueImpulseResponse("Inf"));

        layout.addInteraction("SR",
                new StartWithChecker("Recov"),
                (dis, source, target, time) -> new Pair<>("add", new JSONObject("{'y': 'R', 'n': 1}")));

        layout.addInteraction("SR",
                new StartWithChecker("add agents"),
                (dis, source, target, time) -> new Pair<>("del", new JSONObject("{'y': 'S', 'n': " + dis.get("n") + "}")));


        layout.addInteraction("I", new IsChecker("update"), new InfIn());


        Y0s = Ctrl.generateModelY0("HybridSIR");
    }

    @Test
    public void generate() throws JSONException {
        AbsSimModel Model = Ctrl.generateModel("Hybrid", "HybridSIR", "pHySIR");
        Simulator Sim = new Simulator(Model);
        Sim.addLogPath("log/Hybrid.txt");

        Sim.simulate(Y0s, 0, 30, 1);
        Model.getObserver().getObservations().print();
    }
}