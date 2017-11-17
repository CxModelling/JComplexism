package test;

import dcore.AbsDCore;
import hgm.Director;
import hgm.abmodel.AgentBasedModel;
import hgm.abmodel.Population;
import junit.framework.TestCase;
import mcore.Simulator;

/**
 *
 * Created by TimeWz on 2017/11/10.
 */
public class AgentBasedModelTest extends TestCase {
    Director da = new Director();
    {
        da.loadPCore("script/pSIR.txt");
        da.loadDCore("script/SIR_BN.txt");
    }
    AbsDCore dc = da.generateDCore("SIR_bn", "pSIR");
    AgentBasedModel abm = new AgentBasedModel("Test", dc, null, "Ag");

    {
        abm.addObsState("Sus");
        abm.addObsTransition("Die");
    }

    public void testSimulate() throws Exception {
        Simulator simu = new Simulator(abm, true);

        //simu.simulate();
    }


}