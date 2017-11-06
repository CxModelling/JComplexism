package hgm;

import dcore.AbsDCore;
import dcore.DCoreFactory;
import dcore.IBlueprintDCore;
import dcore.ctbn.BlueprintCTBN;
import dcore.ctmc.BlueprintCTMC;
import hgm.abmodel.BlueprintABM;
import hgm.multimodel.ModelLayout;
import mcore.IMCoreBlueprint;
import org.json.JSONObject;
import pcore.ParameterCore;
import pcore.ScriptException;
import pcore.SimulationModel;
import utils.IO;

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
        String script = IO.loadText(path);
        readPCore(script);
    }

    public void restorePCore(JSONObject js) {
        try {
            addPCore(new SimulationModel(js));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public void restorePCore(String js) {
        restorePCore(new JSONObject(js));
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
        restoreDCore(IO.loadJSON(path));
    }

    public void restoreDCore(JSONObject js) {
        try {
            addDCore((new DCoreFactory()).createFromJSON(js));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
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

    public BlueprintABM createABM(String name, String pc, String dc) {
        BlueprintABM bp = new BlueprintABM(name, pc, dc);
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
