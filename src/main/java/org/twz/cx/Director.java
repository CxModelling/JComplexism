package org.twz.cx;

import org.twz.statespace.AbsDCore;
import org.twz.statespace.DCoreFactory;
import org.twz.statespace.IBlueprintDCore;
import org.twz.statespace.ctbn.BlueprintCTBN;
import org.twz.statespace.ctmc.BlueprintCTMC;
import org.twz.cx.abmodel.ABMBlueprint;
import org.twz.cx.multimodel.ModelLayout;
import org.twz.cx.mcore.IMCoreBlueprint;
import org.json.JSONObject;
import org.twz.dag.ParameterCore;
import org.twz.dag.ScriptException;
import org.twz.dag.SimulationModel;
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class Director {
    private Map<String, SimulationModel> PCores;
    private Map<String, IBlueprintDCore> DCores;
    private Map<String, IMCoreBlueprint> MCores;
    private Map<String, ModelLayout> Layouts;


    public Director() {
        PCores = new HashMap<>();
        DCores = new HashMap<>();
        MCores = new HashMap<>();
        Layouts = new HashMap<>();
    }

    public void addPCore(SimulationModel pc) {
        PCores.putIfAbsent(pc.getName(), pc);
    }

    public void readPCore(String script) {
        try {
            addPCore(new SimulationModel(script));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public void loadPCore(String path) {
        if (path.endsWith(".json")) {
            restorePCore(IO.loadJSON(path));
        } else {
            readPCore(IO.loadText(path));
        }
    }

    public void restorePCore(JSONObject js) {
        try {
            addPCore(new SimulationModel(js));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public void listPCores() {
        System.out.println(PCores.keySet().toString());
    }

    public SimulationModel getPCore(String name) {
        return PCores.get(name);
    }

    public void addDCore(IBlueprintDCore dc) {
        DCores.putIfAbsent(dc.getName(), dc);
    }

    public void loadDCore(String path) {
        if (path.endsWith(".json")) {
            restoreDCore(IO.loadJSON(path));
        } else {
            readDCore(IO.loadText(path));
        }
    }

    public void readDCore(String script) {
        try {
            addDCore(DCoreFactory.createFromScripts(script));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public void restoreDCore(JSONObject js) {
        addDCore(DCoreFactory.createFromJSON(js));
    }

    public void listDCore() {
        System.out.println(DCores.keySet().toString());
    }

    public IBlueprintDCore getDCore(String name) {
        return DCores.get(name);
    }

    public void addMCore(String name, IMCoreBlueprint mc) {
        MCores.putIfAbsent(name, mc);
    }

    public void loadMCore(String path) {
        restoreMCore(IO.loadJSON(path));
    }

    public void restoreMCore(JSONObject js) {
        //try {
        //    addDCore(name, MCoreBuilder.buildFromJSON(js));
        //} catch (ScriptException e) {
        //    e.printStackTrace();
        //}
        // todo
    }

    public void listMCore() {
        System.out.println(MCores.keySet().toString());
    }

    public IMCoreBlueprint getMCore(String name) {
        return MCores.get(name);
    }

    public BlueprintCTBN createCTBN(String name) {
        BlueprintCTBN bp = new BlueprintCTBN(name);
        addDCore(bp);
        return bp;
    }

    public BlueprintCTMC createCTMC(String name) {
        BlueprintCTMC bp = new BlueprintCTMC(name);
        addDCore(bp);
        return bp;
    }

    public ABMBlueprint createABM(String name, String pc, String dc) {
        ABMBlueprint bp = new ABMBlueprint(name, pc, dc);
        addMCore(name, bp);
        return bp;
    }

    public ParameterCore generatePCore(String pc) {
        return getPCore(pc).sampleCore();
    }

    public ParameterCore generatePCore(String pc, Map<String, Double> cond) {
        return getPCore(pc).sampleCore(cond);
    }

    public AbsDCore generateDCore(String dc, String pc) {
        return generateDCore(dc, generatePCore(pc));
    }

    public AbsDCore generateDCore(String dc,ParameterCore pc) {
        IBlueprintDCore bp = getDCore(dc);
        if (bp.isCompatible(pc)) {
            return bp.generateModel(pc);
        } else {
            return null;
        }
    }

}
