import org.twz.dag.ScriptException;
import org.twz.statespace.AbsDCore;
import org.twz.statespace.State;
import org.twz.statespace.ctbn.BlueprintCTBN;
import org.twz.cx.Director;
import junit.framework.TestCase;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class CTBNTest extends TestCase {

    public void testBuildCTBN() {
        Director da = new Director();


        try {
            da.loadPCore("src/test/resources/script/pSIR.txt");
        } catch (ScriptException e) {
            e.printStackTrace();
        }


        BlueprintCTBN bp = da.createCTBN("SIR_bn");

        bp.addMicroState("sir", new String[]{"S", "I", "R"});
        bp.addMicroState("life", new String[]{"Alive", "Dead"});

        bp.addState("Sus", new HashMap<String, String>() {{put("sir", "S"); put("life", "Alive");}});
        bp.addState("Inf", new HashMap<String, String>() {{put("sir", "I"); put("life", "Alive");}});
        bp.addState("Rec", new HashMap<String, String>() {{put("sir", "R"); put("life", "Alive");}});
        bp.addState("Alive", new HashMap<String, String>() {{put("life", "Alive");}});
        bp.addState("Dead", new HashMap<String, String>() {{put("life", "Dead");}});

        bp.addTransition("Die", "Dead");
        bp.addTransition("Infect", "Inf", "beta");
        bp.addTransition("Recov", "Rec", "gamma");

        bp.linkStateTransition("Sus", "Infect");
        bp.linkStateTransition("Inf", "Recov");
        bp.linkStateTransition("Alive", "Die");

        AbsDCore mod = da.generateDCore("SIR_bn", "pSIR");
        State st = mod.getState("Sus");
        assertEquals(st.getName(), "Sus");

        assertFalse(mod.getState("Alive").isa(mod.getState("Sus")));
        assertTrue(mod.getState("Sus").isa(mod.getState("Alive")));
        List<String> ss = new ArrayList<>();
        ss.add("Inf");
        assertEquals(mod.getAccessibleStates(ss).size(), 4);

        System.out.println(bp.toJSON());
    }

    public void testLoadCTBN() {
        Director da = new Director();

        try {
            da.loadPCore("test/resources/script/pSIR.txt");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        da.loadDCore("test/resources/script/SIR_BN.txt");

        AbsDCore mod = da.generateDCore("SIR_bn", "pSIR");
        State st = mod.getState("Sus");
        assertEquals(st.getName(), "Sus");

        assertFalse(mod.getState("Alive").isa(mod.getState("Sus")));
        assertTrue(mod.getState("Sus").isa(mod.getState("Alive")));
        List<String> ss = new ArrayList<>();
        ss.add("Inf");
        assertEquals(mod.getAccessibleStates(ss).size(), 4);


    }

    public void testJosnifyCTBN() throws ScriptException {
        Director da = new Director();

        da.loadPCore("script/pSIR.txt");
        da.loadDCore("script/SIR_BN.txt");

        AbsDCore mod = da.generateDCore("SIR_bn", "pSIR");
        JSONObject js = mod.toJSON();
        js.put("ModelName", "SIR_bn_Copy");
        da.restoreDCore(js);

        mod = da.generateDCore("SIR_bn_Copy", "pSIR");
        State st = mod.getState("Sus");
        assertEquals(st.getName(), "Sus");

        assertFalse(mod.getState("Alive").isa(mod.getState("Sus")));
        assertTrue(mod.getState("Sus").isa(mod.getState("Alive")));
        List<String> ss = new ArrayList<>();
        ss.add("Inf");
        assertEquals(mod.getAccessibleStates(ss).size(), 4);


    }
}
