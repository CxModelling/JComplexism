package org.twz.cx.abmodel.statespace;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsDCore;

import static org.junit.Assert.*;

public class StSpABModelTest {

    private Director Da;
    private StSpABModel Model;
    private AbsDCore DC;
    private ParameterCore PC;
    private StSpY0 Y0;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pDzAB.txt");
        Da.loadDCore("src/test/resources/script/DzAB.txt");

        NodeGroup NG = new NodeGroup("root", new String[0]);
        NG.appendChildren(new NodeGroup("agent", new String[]{"ToA", "ToB","ToB_A"}));
        PC = Da.getBayesNet("pDzAB").toSimulationCore(NG, true).generate("Test");
        DC = Da.generateDCore("DzAB", PC.genPrototype("agent"));


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
    public void simulation() {
        Simulator Simu = new Simulator(Model);
        //Simu.addLogPath("DzAB.txt");
        Y0 = new StSpY0();
        Y0.append(200, "ab");


        Simu.simulate(Y0, 0, 10, 1);
        System.out.println(Model.getObserver().getObservations().toJSON());
    }
}