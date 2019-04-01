package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpPopulation;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsStateSpace;

public class ReincarnationTest {
    private StSpABModel Model;

    @Before
    public void setUp() throws JSONException {
        Director ctrl = new Director();
        ctrl.loadBayesNet("src/test/resources/script/pBAD.txt");
        ctrl.loadStateSpace("src/test/resources/script/BAD.txt");

        NodeGroup NG = new NodeGroup("root", new String[0]);
        NG.appendChildren(new NodeGroup("agent", new String[]{"ToM", "ToO", "Die"}));
        ParameterCore PC = ctrl.getBayesNet("pBAD").toSimulationCore(NG, true).generate("Test");
        AbsStateSpace DC = ctrl.generateDCore("BAD", PC.genPrototype("agent"));


        StSpPopulation Pop = new StSpPopulation("Ag", "agent", DC, PC);
        Model = new StSpABModel("Test", PC, Pop);

        Model.addBehaviour(new Reincarnation("Life", DC.getState("Dead"), DC.getState("Young")));

        Model.addObservingState("Alive");
        Model.addObservingBehaviour("Life");

    }

    @Test
    public void simulation() throws Exception {
        Simulator Simu = new Simulator(Model);
        Simu.onLog("log/Reincarnation.txt");
        StSpY0 y0 = new StSpY0();
        y0.append(1000, "Young");
        y0.append(1000, "Middle");
        y0.append(1000, "Old");

        Simu.simulate(y0, 0, 10, 1);
        Model.print();
        Model.printCounts();
    }
}