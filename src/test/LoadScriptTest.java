package test;

import org.junit.Test;
import hgm.utils.IO;
import pcore.ParameterCore;
import pcore.SimulationModel;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class LoadScriptTest {
    @Test
    public void loadScript() throws Exception {
        String s = IO.loadText("script/pSIR.txt");
        System.out.println(s);
    }

    @Test
    public void usePCore() throws Exception {
        String s = IO.loadText("script/pSIR.txt");

        SimulationModel sm = new SimulationModel(s);
        ParameterCore pc = sm.sampleCore();
        System.out.println(pc);
        System.out.println(pc.getDistribution("Die").sample());

    }
}
