package org.twz.cx.abmodel.statespace;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.behaviour.FDShock;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsDCore;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StSpABMBlueprintTest {
    private Director Da;
    private StSpABMBlueprint Bp;
    private StSpY0 Y0;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Da.loadDCore("src/test/resources/script/CloseSIR.txt");

        Bp = new StSpABMBlueprint("SIR");
        Bp.setAgent("Ag", "agent", "CloseSIR");

        Map<String, Object> args = new HashMap<>();

        args.put("s_src", "Inf");
        args.put("t_tar", "Infect");
        Bp.addBehaviour("FOI", "FDShock", args);
        Bp.setObservations(new String[]{"Sus", "Inf", "Rec"}, new String[]{"Infect"}, new String[]{"FOI"});

        Y0 = new StSpY0();
        Y0.append(950, "Sus");
        Y0.append(50, "Inf");
    }

    @Test
    public void simulation() {
        Map<String, Object> args = new HashMap<>();

        ParameterCore PC = Da.getBayesNet("pCloseSIR")
                .toSimulationCore(Bp.getParameterHierarchy(Da.getDCore("CloseSIR")), true)
                .generate("Test");
        AbsDCore DC = Da.generateDCore("CloseSIR", PC.genPrototype("agent"));

        args.put("pc", PC);
        args.put("dc", DC);

        StSpABModel Model = Bp.generate("Test", args);

        Simulator Simu = new Simulator(Model);
        Simu.addLogPath("FDShock.txt");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }
}