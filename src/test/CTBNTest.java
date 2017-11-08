package test;

import dcore.AbsDCore;
import dcore.State;
import dcore.ctbn.BlueprintCTBN;
import hgm.Director;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class CTBNTest extends TestCase {

    public void testBuildCTBN() throws Exception {
        Director da = new Director();

        da.loadPCore("script/pSIR.txt");

        BlueprintCTBN bp = da.createCTBN("SIR_bn");

        bp.addMicrostate("sir", new String[]{"S", "I", "R"});
        bp.addMicrostate("life", new String[]{"Alive", "Dead"});

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
        System.out.println(st);
        st.getNextTransitions().forEach(System.out::println);

        assertFalse(mod.getState("Alive").isa(mod.getState("Sus")));
        assertTrue(mod.getState("Sus").isa(mod.getState("Alive")));
        List<String> ss = new ArrayList<>();
        ss.add("Inf");
        System.out.println(mod.getAccessibleStates(ss));

    }

    public void testLoadCTBN() throws Exception {
        Director da = new Director();

        da.loadPCore("script/pSIR.txt");
        da.loadDCore("script/SIR_BN.txt");

        AbsDCore mod = da.generateDCore("SIR_bn", "pSIR");
        State st = mod.getState("Sus");
        System.out.println(st);
        st.getNextTransitions().forEach(System.out::println);

        System.out.println(mod.getState("Sus").isa(mod.getState("Alive")));
        List<String> ss = new ArrayList<>();
        ss.add("Inf");
        System.out.println(mod.getAccessibleStates(ss));

    }
}
