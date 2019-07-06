package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.Parameters;
import org.twz.dag.NodeSet;
import org.twz.statespace.AbsStateSpace;

public class StSpABModelTest {

    private StSpABModel Model;

    @Before
    public void setUp() throws Exception {
        Director Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pDzAB.txt");
        Ctrl.loadStateSpace("src/test/resources/script/DzAB.txt");

        NodeSet ns = new NodeSet("root", new String[0]);
        ns.appendChild(new NodeSet("agent", new String[0], new String[]{"ToA", "ToB","ToB_A"}));
        Parameters PC = Ctrl.getBayesNet("pDzAB").toParameterModel(ns).generate("Test");
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
    public void simulation() throws Exception {
        Simulator Simu = new Simulator(Model);
        Simu.onLog("log/DzAB.txt");
        StSpY0 y0 = new StSpY0();
        y0.append("ab", 200);


        Simu.simulate(y0, 0, 10, 1);
        Model.getObserver().getObservations().println();
    }
}