package test;

import junit.framework.TestCase;
import pcore.ParameterCore;
import pcore.SimulationModel;
import utils.IO;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class LoadScriptTest extends TestCase{

    public void testLoadScript() throws Exception {
        String s = IO.loadText("script/pSIR.txt");
        System.out.println(s);
    }


    public void testUsePCore() throws Exception {
        String s = IO.loadText("script/pSIR.txt");

        SimulationModel sm = new SimulationModel(s);
        ParameterCore pc = sm.sampleCore();
        System.out.println(pc);
        System.out.println(pc.getDistribution("Die").sample());

    }
}
