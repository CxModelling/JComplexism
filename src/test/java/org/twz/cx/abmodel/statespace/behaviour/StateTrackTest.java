package org.twz.cx.abmodel.statespace.behaviour;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpPopulation;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.Parameters;
import org.twz.dag.NodeSet;
import org.twz.statespace.AbsStateSpace;

public class StateTrackTest {
    private StSpABModel Model;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Director Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pBAD.txt");
        Ctrl.loadStateSpace("src/test/resources/script/BAD.txt");

        NodeSet ns = new NodeSet("root", new String[0]);
        ns.appendChild(new NodeSet("agent", new String[0], new String[]{"ToM", "ToO", "Die"}));
        Parameters PC = Ctrl.getBayesNet("pBAD").toParameterModel(ns).generate("Test");
        AbsStateSpace DC = Ctrl.generateDCore("BAD", PC.genPrototype("agent"));

        StSpPopulation Pop = new StSpPopulation("Ag", "agent", DC, PC);
        Model = new StSpABModel("Test", PC, Pop);

        Model.addBehaviour(new StateTrack("Mid", DC.getState("Middle")));
        Model.addBehaviour(new StateTrackApprox("MidApp", DC.getState("Middle"), 0.1));


        Model.addObservingState("Middle");
        Model.addObservingBehaviour("Mid");
        Model.addObservingBehaviour("MidApp");
        Y0 = new StSpY0();
        Y0.append("Young", 50);
        Y0.append("Middle", 50);
        Y0.append("Old", 50);

    }

    @Test
    public void simulation() throws Exception {
        Simulator Simu = new Simulator(Model);
        Simu.onLog("log/StateTrack.txt");
        Simu.simulate(Y0, 0, 10, 1);
        Model.print();
    }
}