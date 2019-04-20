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
import org.twz.dag.Parameters;
import org.twz.dag.util.NodeGroup;
import org.twz.dag.util.NodeSet;
import org.twz.statespace.AbsStateSpace;

public class FDShockTest {

    private StSpABModel Model;

    @Before
    public void setUp() throws JSONException {
        Director Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Ctrl.loadStateSpace("src/test/resources/script/CloseSIR.txt");

        NodeSet ns = new NodeSet("root", new String[0]);
        ns.appendChild(new NodeSet("agent", new String[]{"beta", "gamma"}));
        Parameters PC = Ctrl.getBayesNet("pCloseSIR").toParameterModel(ns).generate("Test");
        AbsStateSpace DC = Ctrl.generateDCore("CloseSIR", PC.genPrototype("agent"));


        StSpPopulation Pop = new StSpPopulation("Ag", "agent", DC, PC);
        Model = new StSpABModel("Test", PC, Pop);
        Model.addBehaviour(new FDShock("FOI", DC.getState("Inf"), DC.getTransition("Infect")));

        Model.addObservingState("Sus");
        Model.addObservingState("Inf");
        Model.addObservingState("Rec");
        Model.addObservingTransition("Infect");
        Model.addObservingBehaviour("FOI");

    }

    @Test
    public void simulation() throws Exception {
        Simulator Simu = new Simulator(Model);
        //Simu.addLogPath("log/FDShock.txt");
        StSpY0 y0 = new StSpY0();
        y0.append(90, "Sus");
        y0.append(10, "Inf");

        Simu.simulate(y0, 0, 10, 1);
        Model.print();
    }
}