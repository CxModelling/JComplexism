package org.twz.cx.abmodel.statespace;

import org.junit.Test;
import org.junit.Before;
import org.twz.cx.Director;
import org.twz.cx.element.Event;
import org.twz.dag.ParameterCore;
import org.twz.statespace.AbsDCore;

import static org.junit.Assert.*;

public class StSpAgentTest {

    private Director Da;
    private StSpAgent Agent;
    private AbsDCore DC;
    private ParameterCore PC;

    @Before
    public void setUp() {
        Da = new Director();
        Da.loadBayesNet("src/test/resources/script/pSIR.txt");
        Da.loadDCore("src/test/resources/script/SIR_BN.txt");
        PC = Da.generatePCore("Test","pSIR");
        DC = Da.generateDCore("SIR_bn", PC);
        Agent = new StSpAgent("Test", PC, DC.getState("Sus"));
        Agent.initialise(0, null);
    }

    @Test
    public void getState() {
        assertEquals(Agent.getState(), DC.getState("Sus"));
    }

    @Test
    public void findNext() {
        System.out.println(Agent.findNext());
    }

    @Test
    public void executeEvent() {
        Event event = Agent.findNext();
        Agent.executeEvent();
        if (event.getValue() == DC.getTransition("Infect")) {
            assertEquals(Agent.getState(), DC.getState("Inf"));
        } else {
            assertTrue(Agent.isa(DC.getState("Dead")));
        }
    }

    @Test
    public void isa() {
        assertTrue(Agent.isa(DC.getState("Alive")));
    }
}