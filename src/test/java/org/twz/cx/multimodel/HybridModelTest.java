package org.twz.cx.multimodel;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.ebmodel.*;
import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Simulator;
import org.twz.cx.mcore.communicator.*;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.dataframe.Pair;
import org.twz.prob.IDistribution;
import org.twz.prob.Poisson;
import org.twz.statespace.AbsStateSpace;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 14/08/2018.
 */

class InfIn implements IShocker {
    private double Last;

    public InfIn() {
        Last = Double.NaN;
    }

    @Override
    public Pair<String, JSONObject> shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time) {
        Pair<String, JSONObject> res = null;
        if (!Double.isNaN(Last)) {
            AbsEquations eq = ((EquationBasedModel) source).getEquations();
            double dt = time - Last;
            double sus = eq.getY("S"), rec = eq.getY("R"), inf = target.getSnapshot("StInf", time);
            double lam = source.getParameter("transmission_rate") * sus * inf * dt / (sus + rec + inf);
            IDistribution di = new Poisson(lam);
            int n = (int) Math.min(di.sample(), sus);
            System.out.println(n);
            res = new Pair<>("InfIn", new JSONObject(String.format("{'n': %d}", n)));
        }
        Last = time;
        return res;
    }
}


public class HybridModelTest {

    private Director Da;
    private MultiModel Model;
    private Y0s Y0;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pHybridSIR.txt");
        Da.loadStateSpace("src/test/resources/script/HybridSIR.txt");

        StSpABMBlueprint BpA = new StSpABMBlueprint("abm");
        BpA.setAgent("Ag", "agent", "HySIR");

        BpA.addBehaviour("{'Name': 'Recovery', 'Type': 'Cohort', 'Args': {'s_death': 'Rec'}}");
        BpA.addBehaviour("{'Name': 'StInf', 'Type': 'StateTrack', 'Args': {'s_src': 'Inf'}}");
        BpA.addBehaviour("{'Name': 'InfIn', 'Type': 'AgentImport', 'Args': {'s_birth': 'Inf'}}");
        BpA.setObservations(new String[]{"Inf"}, new String[]{"Recov"}, new String[]{"InfIn", "StInf"});


        ODEEBMBlueprint BpE = new ODEEBMBlueprint("ebm");
        BpE.setODE((t, y0, y1, parameters, attributes) -> {
            y1[0] = 0;
            y1[1] = 0;
        }, new String[]{"S", "R"});

        BpE.appendExternalVariable("Inf", 0);
        BpE.addMeasurementFunction((tab, ti, ys, pc, x) -> {
            double inf = x.getDouble("Inf");
            double n = ys[0] + ys[1] + inf;
            tab.put("Prv", inf/n);
            tab.put("N", n);
        });
        BpE.setObservations(new String[]{"S", "R"});

        Y0 = new Y0s();

        StSpY0 y0a = new StSpY0();
        y0a.append(50, "Inf");
        Y0.appendChildren("I", y0a);

        EBMY0 y0e = new EBMY0();
        y0e.append("{'y': 'S', 'n': 950}");
        Y0.appendChildren("SR", y0e);


        NodeGroup ng = new NodeGroup("root", new String[0]);
        ng.appendChildren(BpA.getParameterHierarchy(Da.getStateSpace("HySIR")));
        ng.appendChildren(BpE.getParameterHierarchy(null));

        ng.print();

        ParameterCore PC = Da.getBayesNet("pHySIR").toSimulationCore(ng, true).generate("Test");
        AbsStateSpace DC;


        Model = new MultiModel("MM", PC);

        Map<String, Object> args;
        args = new HashMap<>();
        ParameterCore PCA = PC.breed("I", "abm");

        DC = Da.generateDCore("HySIR", PCA.genPrototype("agent"));
        args.put("dc", DC);
        args.put("pc", PC);

        AbsSimModel m_i = BpA.generate("I", args);

        Model.appendModel(m_i);

        args = new HashMap<>();
        args.put("dc", DC);
        args.put("pc", PC.breed("SR", "ebm"));

        AbsSimModel m_sr = BpE.generate("SR", args);

        Model.appendModel(m_sr);

        Model.addObservingModel("SR");
        Model.addObservingModel("I");


        m_sr.addListener(new StartWithChecker("update value"), new ValueImpulseShocker("Inf"));
        m_sr.addListener(new StartWithChecker("Recov"), (dis, source, target, time) ->
            new Pair<>("add", new JSONObject("{'y': 'R', 'n': 1}"))
        );
        m_sr.addListener(new StartWithChecker("add agents"), (dis, source, target, time) ->
            new Pair<>("del", new JSONObject("{'y': 'S', 'n': " + dis.get("n") + "}"))
        );


        m_i.addListener(new IsChecker("update"), new InfIn());
    }

    @Test
    public void simulation() {
        Simulator Sim = new Simulator(Model);
        Sim.addLogPath("log/Hybrid.txt");

        Sim.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }

}