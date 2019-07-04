package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.Parameters;
import org.twz.dag.NodeSet;
import org.twz.statespace.AbsStateSpace;

import java.util.HashMap;
import java.util.Map;

public class StSpABMBlueprintTest {
    private Director Ctrl;
    private StSpABMBlueprint Bp;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Ctrl.loadStateSpace("src/test/resources/script/CloseSIR.txt");

        Bp = new StSpABMBlueprint("SIR");
        Bp.setAgent("Ag", "agent", "CloseSIR");

        Map<String, Object> args = new HashMap<>();

        args.put("s_src", "Inf");
        args.put("t_tar", "Infect");
        Bp.addBehaviour("FOI", "FDShock", args);
        Bp.setObservations(new String[]{"Sus", "Inf", "Rec"}, new String[]{"Infect"}, new String[]{"FOI"});

        Y0 = new StSpY0();
        Y0.append("Sus", 950);
        Y0.append("Inf", 50);
    }

    @Test
    public void simulationPcDc() throws Exception {
        Map<String, Object> args = new HashMap<>();
        NodeSet ns = Bp.getParameterHierarchy(Ctrl);

        Parameters PC = Ctrl.getBayesNet("pCloseSIR")
                .toParameterModel(ns)
                .generate("Test");
        AbsStateSpace DC = Ctrl.generateDCore("CloseSIR", PC.genPrototype("agent"));

        args.put("pc", PC);
        args.put("dc", DC);

        run(args);
    }

    @Test
    public void simulationDaBN() throws Exception {
        Map<String, Object> args = new HashMap<>();

        args.put("bn", "pCloseSIR");
        args.put("da", Ctrl);

        run(args);
    }

    public void run(Map<String, Object> args) throws Exception {
        StSpABModel Model = Bp.generate("Test", args);

        Simulator Simu = new Simulator(Model);
        //Simu.addLogPath("log/FDShock.txt");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }
}