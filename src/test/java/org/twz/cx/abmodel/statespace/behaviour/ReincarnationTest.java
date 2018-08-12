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

public class ReincarnationTest {
    private Director Da;
    private StSpABModel Model;
    private AbsDCore DC;
    private ParameterCore PC;
    private StSpY0 Y0;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pBAD.txt");
        Da.loadDCore("src/test/resources/script/BAD.txt");

        NodeGroup NG = new NodeGroup("root", new String[0]);
        NG.appendChildren(new NodeGroup("agent", new String[]{"ToM", "ToO", "Die"}));
        PC = Da.getBayesNet("pBAD").toSimulationCore(NG, true).generate("Test");
        DC = Da.generateDCore("BAD", PC.genPrototype("agent"));


        StSpPopulation Pop = new StSpPopulation("Ag", "agent", DC, PC);
        Model = new StSpABModel("Test", PC, Pop);
        Model.addBehaviour(new Reincarnation("Life", DC.getState("Dead"), DC.getState("Young")));

        Model.addObservingState("Alive");
        Model.addObservingBehaviour("Life");

    }

    @Test
    public void simulation() {
        Simulator Simu = new Simulator(Model);
        Simu.addLogPath("log/Reincarnation.txt");
        Y0 = new StSpY0();
        Y0.append(50, "Young");
        Y0.append(50, "Middle");
        Y0.append(50, "Old");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }
}