package org.twz.cx.multimodel;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.ebmodel.*;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.cx.mcore.communicator.*;
import org.twz.dag.util.NodeGroup;
import org.twz.dataframe.Pair;
import org.twz.prob.IDistribution;
import org.twz.prob.Poisson;

/**
 *
 * Created by TimeWz on 14/08/2018.
 */

class InfIn implements IResponse {
    private double Last;

    public InfIn() {
        Last = Double.NaN;
    }

    @Override
    public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) throws JSONException {
        Pair<String, JSONObject> res = null;
        if (!Double.isNaN(Last)) {
            AbsEquations eq = ((EquationBasedModel) source).getEquations();
            double dt = time - Last;
            double sus = eq.getY("S"), rec = eq.getY("R"), inf = target.getSnapshot("StInf", time);
            double lam = source.getParameter("transmission_rate") * sus * inf * dt / (sus + rec + inf);
            IDistribution di = new Poisson(lam);
            int n = (int) Math.min(di.sample(), sus);
            res = new Pair<>("InfIn", new JSONObject(String.format("{'n': %d}", n)));
        }
        Last = time;
        return res;
    }
}


public class HybridModelTest {

    private Director Da;
    private MultiModel Model;
    private IY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pHybridSIR.txt");
        Da.loadStateSpace("src/test/resources/script/HybridSIR.txt");

        StSpABMBlueprint BpA = (StSpABMBlueprint) Da.createSimModel("abm", "StSpABM");
        BpA.setAgent("Ag", "agent", "HySIR");

        BpA.addBehaviour("{'Name': 'Recovery', 'Type': 'Cohort', 'Args': {'s_death': 'Rec'}}");
        BpA.addBehaviour("{'Name': 'StInf', 'Type': 'StateTrack', 'Args': {'s_src': 'Inf'}}");
        BpA.addBehaviour("{'Name': 'InfIn', 'Type': 'AgentImport', 'Args': {'s_birth': 'Inf'}}");
        BpA.setObservations(new String[]{"Inf"}, new String[]{"Recov"}, new String[]{"InfIn", "StInf"});


        ODEEBMBlueprint BpE = (ODEEBMBlueprint) Da.createSimModel("ebm", "ODEEBM");
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

        ModelLayout layout = Da.createModelLayout("Hybrid");


        EBMY0 y0e = new EBMY0();
        y0e.append("{'y': 'S', 'n': 950}");
        layout.addEntry("SR", "ebm", y0e);

        StSpY0 y0a = new StSpY0();
        y0a.append(50, "Inf");
        layout.addEntry("I", "abm", y0a);


        NodeGroup ng = Da.getParameterHierarchy("Hybrid");

        ng.print();


        Model = (MultiModel) Da.generateModel("SIR", "Hybrid", "pHySIR");


        AbsSimModel m_i = Model.getModel("I");
        AbsSimModel m_sr = Model.getModel("SR");


        m_sr.addListener(new StartWithChecker("update value"), new ValueImpulseResponse("Inf"));
        m_sr.addListener(new StartWithChecker("Recov"), (dis, source, target, time) ->
            new Pair<>("add", new JSONObject("{'y': 'R', 'n': 1}"))
        );
        m_sr.addListener(new StartWithChecker("add agents"), (dis, source, target, time) ->
            new Pair<>("del", new JSONObject("{'y': 'S', 'n': " + dis.get("n") + "}"))
        );


        m_i.addListener(new IsChecker("update"), new InfIn());

        Y0 = Da.generateModelY0("Hybrid");
    }

    @Test
    public void simulation() throws JSONException {
        Simulator Sim = new Simulator(Model);
        Sim.addLogPath("log/Hybrid.txt");

        Sim.simulate(Y0, 0, 30, 1);
        Model.getObserver().getObservations().print();
    }

}