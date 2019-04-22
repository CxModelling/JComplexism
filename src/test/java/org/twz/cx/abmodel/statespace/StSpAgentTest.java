package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.junit.Test;
import org.junit.Before;
import org.twz.cx.Director;
import org.twz.cx.element.Event;
import org.twz.dag.Parameters;
import org.twz.statespace.AbsStateSpace;

import static org.junit.Assert.*;

public class StSpAgentTest {

    private StSpAgent Agent;
    private AbsStateSpace DC;

    @Before
    public void setUp() throws JSONException {
        Director ctrl = new Director();
        ctrl.loadBayesNet("src/test/resources/script/pSIR.txt");
        ctrl.loadStateSpace("src/test/resources/script/SIR_BN.txt");
        Parameters PC = ctrl.generatePCore("Test", "pSIR");
        DC = ctrl.generateDCore("SIR_bn", PC);
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