import org.twz.statespace.AbsDCore;
import org.twz.cx.Director;
import org.twz.cx.abmodel.ABModel;
import junit.framework.TestCase;
import org.twz.cx.mcore.Simulator;

/**
 *
 * Created by TimeWz on 2017/11/10.
 */
public class ABModelTest extends TestCase {
    Director da = new Director();
    {
        da.loadPCore("script/pSIR.txt");
        da.loadDCore("script/SIR_BN.txt");
    }
    AbsDCore dc = da.generateDCore("SIR_bn", "pSIR");
    StSpABModel abm = new ABModel("Test", dc, null, "Ag");

    {
        abm.addObsState("Sus");
        abm.addObsTransition("Die");
    }

    public void testSimulate() {
        Simulator simu = new Simulator(abm, true);

        //simu.simulate();
    }


}