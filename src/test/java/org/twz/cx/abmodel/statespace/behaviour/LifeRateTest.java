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

public class LifeRateTest {
    private StSpABModel Model;
    private StSpY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Director Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pBAD.txt");
        Ctrl.loadStateSpace("src/test/resources/script/BAD.txt");

        NodeGroup NG = new NodeGroup("root", new String[0]);
        NG.appendChildren(new NodeGroup("agent", new String[]{"ToM", "ToO", "Die"}));
        ParameterCore PC = Ctrl.getBayesNet("pBAD").toSimulationCore(NG, true).generate("Test");
        AbsStateSpace DC = Ctrl.generateDCore("BAD", PC.genPrototype("agent"));

        StSpPopulation Pop = new StSpPopulation("Ag", "agent", DC, PC);
        Model = new StSpABModel("Test", PC, Pop);

        Model.addBehaviour(new LifeRate("Life", DC.getState("Dead"), DC.getState("Young"), 0.5, 0.1));

        Model.addObservingState("Alive");
        Model.addObservingBehaviour("Life");

        Y0 = new StSpY0();
        Y0.append(50, "Young");
        Y0.append(50, "Middle");
        Y0.append(50, "Old");

    }

    @Test
    public void simulation() throws JSONException {
        Simulator Simu = new Simulator(Model);
        Simu.addLogPath("log/LifeRate.txt");
        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }
}