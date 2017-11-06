package test;

import dcore.AbsDCore;
import dcore.IBlueprintDCore;
import dcore.State;
import dcore.ctbn.BlueprintCTBN;
import hgm.Director;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
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

        bp.addState("Sus", new int[]{0, 0});
        bp.addState("Inf", new int[]{1, 0});
        bp.addState("Rec", new int[]{2, 0});
        bp.addState("Alive", new int[]{-1, 0});
        bp.addState("Dead", new int[]{-1, 1});

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

        System.out.println(mod.getState("Sus").isa(mod.getState("Alive")));
        List<String> ss = new ArrayList<>();
        ss.add("Inf");
        System.out.println(mod.getAccessibleStates(ss));

    }

    public void testLoadCTBN() throws Exception {
        Director da = new Director();

        da.loadPCore("script/pSIR.txt");
        da.loadDCore("script/SIR_BN.txt");
        da.re
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
