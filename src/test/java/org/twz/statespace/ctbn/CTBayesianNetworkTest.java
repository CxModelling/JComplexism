package org.twz.statespace.ctbn;

import org.junit.Before;
import org.junit.Test;
import org.twz.dag.BayesNet;
import org.twz.dag.ScriptException;
import org.twz.io.IO;
import org.twz.statespace.DCoreFactory;
import org.twz.statespace.IBlueprintDCore;
import org.twz.statespace.State;

import static org.junit.Assert.*;


/**
 *
 * Created by TimeWz on 10/08/2018.
 */
public class CTBayesianNetworkTest  {
    CTBayesianNetwork CTBN;


    @Before
    public void setUp() {
        IBlueprintDCore bp;
        try {
            bp = DCoreFactory.createFromScripts(IO.loadText("src/test/resources/script/SIR_BN.txt"));
        } catch (ScriptException e) {
            bp = new BlueprintCTBN("SIR");
        }

        BayesNet bn = new BayesNet("pSIR");
        bn.appendLoci("beta ~ exp(1.5)");
        bn.appendLoci("gamma ~ exp(0.2)");
        bn.appendLoci("Die ~ exp(0.02)");

        bp.generateModel(bn.toSimulationCore().generate(""));
        CTBN = (CTBayesianNetwork) bp.generateModel(bn.toSimulationCore().generate(""));
    }

    @Test
    public void testGetStateSpace() throws Exception {
        System.out.println(CTBN.getStateSpace().keySet());
    }

    @Test
    public void testGetWellDefinedStateSpace() throws Exception {
        System.out.println(CTBN.getWellDefinedStateSpace().keySet());
    }

    @Test
    public void testGetTransitionSpace() throws Exception {
        System.out.println(CTBN.getTransitionSpace().keySet());
    }

    @Test
    public void testIsa() throws Exception {
        State sus = CTBN.getState("Sus");
        assertTrue(sus.isa(sus));
        assertTrue(sus.isa(CTBN.getState("Alive")));
        assertFalse(sus.isa(CTBN.getState("Rec")));
    }

    @Test
    public void testExec() throws Exception {
        State sus = CTBN.getState("Sus");
        assertEquals(sus.exec(CTBN.getTransition("Infect")), CTBN.getState("Inf"));
    }

}