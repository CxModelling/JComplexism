package org.twz.cx.abmodel.statespace.behaviour;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABModel;
import org.twz.cx.abmodel.statespace.StSpPopulation;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsDCore;

import static org.junit.Assert.*;

public class FDShockTest {

    private Director Da;
    private StSpABModel Model;
    private AbsDCore DC;
    private ParameterCore PC;
    private StSpY0 Y0;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Da.loadDCore("src/test/resources/script/CloseSIR.txt");

        NodeGroup NG = new NodeGroup("root", new String[0]);
        NG.appendChildren(new NodeGroup("agent", new String[]{"beta", "gamma"}));
        PC = Da.getBayesNet("pCloseSIR").toSimulationCore(NG, true).generate("Test");
        DC = Da.generateDCore("CloseSIR", PC.genPrototype("agent"));


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
    public void simulation() {
        Simulator Simu = new Simulator(Model);
        Simu.addLogPath("FDShock.txt");
        Y0 = new StSpY0();
        Y0.append(250, "Sus");
        Y0.append(50, "Inf");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }
}