package org.twz.cx;

import org.twz.dag.BayesNet;
import org.twz.dag.SimulationCore;
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
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class Director {
    private Map<String, BayesNet> BNs;
    private Map<String, IBlueprintDCore> DCores;
    private Map<String, IMCoreBlueprint> MCores;
    private Map<String, ModelLayout> Layouts;
    private Logger Log;

    public Director() {
        BNs = new HashMap<>();
        DCores = new HashMap<>();
        MCores = new HashMap<>();
        Layouts = new HashMap<>();
        Log = Logger.getLogger("Main");
        Log.addHandler(new ConsoleHandler());
    }

    public void addBayesNet(BayesNet bn) {
        if (BNs.putIfAbsent(bn.getName(), bn) != null) {
            Log.info("New BayesNet " + bn.getName() + " added");
        }

    }

    public void readBayesNet(String script) {
        try {
            addBayesNet(BayesNet.buildFromScript(script));
        } catch (ScriptException e) {
            Log.warning("Invalidated script");
        }
    }

    public void readBayesNet(JSONObject js) {
        try {
            addBayesNet(new BayesNet(js));
        } catch (ScriptException e) {
            Log.warning("Invalidated script");
        }
    }

    public void loadBayesNet(String path) {
        if (path.endsWith(".json")) {
            readBayesNet(IO.loadJSON(path));
        } else {
            readBayesNet(IO.loadText(path));
        }
    }

    public void listBayesNets() {
        System.out.println(BNs.keySet());
    }

    public BayesNet getBayesNet(String name) {
        return BNs.get(name);
    }

    public void addDCore(IBlueprintDCore dc) {
        if(DCores.putIfAbsent(dc.getName(), dc)!=null) {
            Log.info("New Dynamic Core " + dc.getName() + " added");
        }
    }

    public void loadDCore(String path) {
        if (path.endsWith(".json")) {
            readDCore(IO.loadJSON(path));
        } else {
            readDCore(IO.loadText(path));
        }
    }

    public void readDCore(String script) {
        try {
            addDCore(DCoreFactory.createFromScripts(script));
        } catch (ScriptException e) {
            Log.warning("Invalidated script");
        }
    }

    public void readDCore(JSONObject js) {
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

    public BayesNet createBN(String name) {
        BayesNet bn = new BayesNet(name);
        addBayesNet(bn);
        return bn;
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

    public ParameterCore generatePCore(String name, String bn) {
        return getBayesNet(bn).toSimulationCore().generate(name);
    }

    public AbsDCore generateDCore(String dc, String pc) {
        return generateDCore(dc, generatePCore(dc, pc));
    }

    public AbsDCore generateDCore(String dc, ParameterCore pc) {
        IBlueprintDCore bp = getDCore(dc);
        if (bp.isCompatible(pc)) {
            return bp.generateModel(pc);
        } else {
            return null;
        }
    }

}
