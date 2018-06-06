import junit.framework.TestCase;
import org.twz.dag.ParameterCore;
import org.twz.dag.ScriptException;
import org.twz.dag.SimulationModel;
import org.twz.io.IO;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class LoadScriptTest extends TestCase{

    public void testLoadScript() {
        String s = IO.loadText("script/pSIR.txt");
        System.out.println(s);
    }


    public void testUsePCore() throws ScriptException {
        String s = IO.loadText("script/pSIR.txt");

        SimulationModel sm = new SimulationModel(s);
        ParameterCore pc = sm.sampleCore();
        System.out.println(pc);
        System.out.println(pc.getDistribution("Die").sample());

    }
}
