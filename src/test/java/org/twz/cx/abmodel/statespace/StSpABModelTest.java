package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsStateSpace;

public class StSpABModelTest {

    private StSpABModel Model;

    @Before
    public void setUp() throws JSONException {
        Director Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pDzAB.txt");
        Ctrl.loadStateSpace("src/test/resources/script/DzAB.txt");

        NodeGroup NG = new NodeGroup("root", new String[0]);
        NG.appendChildren(new NodeGroup("agent", new String[]{"ToA", "ToB","ToB_A"}));
        ParameterCore PC = Ctrl.getBayesNet("pDzAB").toSimulationCore(NG, true).generate("Test");
        AbsStateSpace DC = Ctrl.generateDCore("DzAB", PC.genPrototype("agent"));


        StSpPopulation Pop = new StSpPopulation("Ag", "agent", DC, PC);
        Model = new StSpABModel("Test", PC, Pop);
        Model.addObservingTransition("ToA");
        Model.addObservingTransition("ToB");
        Model.addObservingTransition("ToB_A");
        Model.addObservingState("ab");
        Model.addObservingState("aB");
        Model.addObservingState("Ab");
        Model.addObservingState("AB");

    }

    @Test
    public void simulation() throws JSONException {
        Simulator Simu = new Simulator(Model);
        Simu.onLog("log/DzAB.txt");
        StSpY0 y0 = new StSpY0();
        y0.append(200, "ab");


        Simu.simulate(y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }
}