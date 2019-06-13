package org.twz.cx.multimodel;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.Director;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 14/08/2018.
 */
public class MultiModelTest {

    private Director Ctrl;

    private IY0 Y0;

    @Before
    public void setUp() throws JSONException {
        Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/script/pCloseSIR.txt");
        Ctrl.loadStateSpace("src/test/resources/script/CloseSIR.txt");


        StSpABMBlueprint Bp = (StSpABMBlueprint) Ctrl.createSimModel("abm", "StSpABM");
        Bp.setAgent("Ag", "agent", "CloseSIR");

        Map<String, Object> args = new HashMap<>();
        args.put("s_src", "Inf");
        args.put("t_tar", "Infect");

        Bp.addBehaviour("FOI", "FDShock", args);
        Bp.setObservations(new String[]{"Sus", "Inf", "Rec"}, new String[]{"Infect"}, new String[]{"FOI"});


        ModelLayout lyo = Ctrl.createModelLayout("MultiSIR");

        StSpY0 y0 = new StSpY0();
        y0.append("Sus", 950);
        y0.append("Inf", 50);
        lyo.addEntry("M", "abm", y0, 1, 2);

        Y0 = Ctrl.generateModelY0("MultiSIR");
    }

    @Test
    public void simulation() throws Exception {
        AbsSimModel Model = Ctrl.generateModel("2SIR", "MultiSIR", "pCloseSIR");
        Simulator Simu = new Simulator(Model);
        Simu.onLog("log/MM.txt");

        Simu.simulate(Y0, 0, 10, 1);
        Model.getObserver().getObservations().print();
    }

}