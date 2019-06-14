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
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.IWalkable;
import org.twz.prob.Poisson;

public class ModelLayoutTest {
    class InfIn implements IResponse {
        private double Last;

        public InfIn() {
            Last = Double.NaN;
        }

        @Override
        public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) throws JSONException, IncompleteConditionException {
            Pair<String, JSONObject> res = null;
            if (!Double.isNaN(Last)) {
                AbsEquations eq = ((EquationBasedModel) source).getEquations();
                double dt = time - Last;
                double sus = eq.getY("S"), rec = eq.getY("R"), inf = target.getSnapshot("StInf", time);
                double lam = source.getParameter("transmission_rate") * sus * inf * dt / (sus + rec + inf);
                IWalkable di = new Poisson(lam);
                int n = (int) Math.min(di.sample(), sus);
                res = new Pair<>("InfIn", new JSONObject(String.format("{'n': %d}", n)));
            }
            Last = time;
            return res;
        }
    }


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
                inf = (double) x.get("Inf");
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            double n = ys[0] + ys[1] + inf;
            tab.put("Prv", inf/n);
            tab.put("N", n);
        });
        BpE.setObservations(new String[]{"S", "R"});
        BpE.setDt(0.5);


        ModelLayout layout = Ctrl.createModelLayout("HybridSIR");
        EBMY0 y0e = new EBMY0();
        y0e.append("{'y': 'S', 'n': 950}");
        layout.addEntry("SR", "ebm", y0e);

        StSpY0 y0a = new StSpY0();
        y0a.append("Inf", 50);
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


        layout.addInteraction("I", new InclusionChecker(new String[] {"update", "initialise"}), new InfIn());


        Y0s = Ctrl.generateModelY0("HybridSIR");
    }

    @Test
    public void generate() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("Hybrid", "HybridSIR", "pHySIR");
        Simulator Sim = new Simulator(Model);
        Sim.onLog("log/Hybrid.txt");

        Sim.simulate(Y0s, 5, 30, 0.25);
        Model.getObserver().getObservations().println();
    }
}