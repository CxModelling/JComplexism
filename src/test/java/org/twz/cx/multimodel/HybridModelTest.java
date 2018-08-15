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
import org.twz.cx.mcore.communicator.IShocker;
import org.twz.cx.mcore.communicator.InitialChecker;
import org.twz.cx.mcore.communicator.StartWithChecker;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.dataframe.Pair;
import org.twz.statespace.AbsStateSpace;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 14/08/2018.
 */

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
        BpA.setObservations(new String[]{"Inf", "Rec"}, new String[]{"Recov"}, new String[]{"InfIn", "StInf"});


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

        m_sr.addListener(new InitialChecker(), (dis, source, target, time) -> null);
    }

    @Test
    public void simulation() {
        Simulator Simu = new Simulator(Model);
        Simu.addLogPath("log/Hybrid.txt");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }

}