package test;

import dcore.State;
import dcore.Transition;
import dcore.ctbn.BlueprintCTBN;
import dcore.ctbn.ModelCTBN;
import main.Utils;
import org.junit.Test;
import pcore.ParameterCore;
import pcore.SimulationModel;

import java.io.File;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class CTBNTest {

    @Test
    public void buildCTBN() throws Exception {
        String path = new File("script/pSIR.txt").getCanonicalPath();
        String s = Utils.loadText(path);

        SimulationModel sm = new SimulationModel(s);
        ParameterCore pc = sm.sampleCore();

        BlueprintCTBN bp = new BlueprintCTBN("SIR_bn");

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

        ModelCTBN mod = bp.generateModel(pc, "Test1");
        State st = mod.getState("Sus");
        System.out.println(st);
        for (Transition tr: st.getNextTransitions()) {
            System.out.println(tr);
        }

    }
}
