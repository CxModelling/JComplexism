package org.twz.cx.multimodel;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.BranchY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsStateSpace;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 14/08/2018.
 */
public class MultiModelTest {

    private Director Da;
    private MultiModel Model;
    private AbsStateSpace DC;
    private ParameterCore PC;
    private BranchY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Da.loadStateSpace("src/test/resources/script/CloseSIR.txt");

        StSpABMBlueprint Bp = new StSpABMBlueprint("abm");
        Bp.setAgent("Ag", "agent", "CloseSIR");

        Map<String, Object> args = new HashMap<>();

        args.put("s_src", "Inf");
        args.put("t_tar", "Infect");
        Bp.addBehaviour("FOI", "FDShock", args);
        Bp.setObservations(new String[]{"Sus", "Inf", "Rec"}, new String[]{"Infect"}, new String[]{"FOI"});


        NodeGroup ng = new NodeGroup("root", new String[0]);
        ng.appendChildren(Bp.getParameterHierarchy(Da));

        PC = Da.getBayesNet("pCloseSIR").toSimulationCore(ng, true).generate("Test");
        DC = Da.generateDCore("CloseSIR", PC.genPrototype("agent"));


        Model = new MultiModel("MM", PC);

        args = new HashMap<>();
        PC = PC.breed("m1", "abm");
        DC = Da.generateDCore("CloseSIR", PC.genPrototype("agent"));
        args.put("dc", DC);
        args.put("pc", PC);

        Model.appendModel(Bp.generate("m1", args));

        args = new HashMap<>();
        PC = PC.genSibling("m2");
        DC = Da.generateDCore("CloseSIR", PC.genPrototype("agent"));
        args.put("dc", DC);
        args.put("pc", PC);

        Model.appendModel(Bp.generate("m2", args));

        Model.addObservingModel("m1");
        Model.addObservingModel("m2");


        StSpY0 y0 = new StSpY0();
        y0.append(950, "Sus");
        y0.append(50, "Inf");

        Y0 = new BranchY0();
        Y0.appendChildren("m1", y0);
        Y0.appendChildren("m2", y0);
    }

    @Test
    public void simulation() throws JSONException {
        Simulator Simu = new Simulator(Model);
        Simu.addLogPath("log/MM.txt");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }

}