package test;

import dcore.AbsDCore;
import hgm.Director;
import hgm.abmodel.Population;
import junit.framework.TestCase;

/**
 * Created by TimeWz on 2017/11/10.
 */
public class PopulationTest extends TestCase {
    Director da = new Director();
    {
        da.loadPCore("script/pSIR.txt");
        da.loadDCore("script/SIR_BN.txt");
    }
    AbsDCore dc = da.generateDCore("SIR_bn", "pSIR");
    Population Pop = new Population(dc);


    public void testAddAgents() throws Exception {
        Pop.addAgents("Sus", 5);
        Pop.addAgents("Inf", 5);
        assertEquals(5, Pop.count(dc.getState("Sus")));
        assertEquals(5, Pop.count(dc.getState("Inf")));
        assertEquals(10, Pop.count());
    }

}