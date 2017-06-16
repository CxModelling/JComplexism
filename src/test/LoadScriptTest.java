package test;

import org.junit.Test;
import main.Utils;
import pcore.DirectedAcyclicGraph;
import pcore.ParameterCore;
import pcore.SimulationModel;

import java.io.File;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class LoadScriptTest {
    @Test
    public void loadScript() throws Exception {
        String path = new File("script/pSIR.txt").getCanonicalPath();
        String s = Utils.loadText(path);
        System.out.println(s);
    }

    @Test
    public void usePCore() throws Exception {
        String path = new File("script/pSIR.txt").getCanonicalPath();
        String s = Utils.loadText(path);

        SimulationModel sm = new SimulationModel(s);
        ParameterCore pc = sm.sampleCore();
        System.out.println(pc);
        System.out.println(pc.getDistribution("Die").sample());

    }
}
