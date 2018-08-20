package org.twz.cx;

import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IModelBlueprint;
import org.twz.cx.mcore.IY0;
import org.twz.dag.BayesNet;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsStateSpace;
import org.twz.statespace.StateSpaceFactory;
import org.twz.statespace.IStateSpaceBlueprint;
import org.twz.statespace.ctbn.CTBNBlueprint;
import org.twz.statespace.ctmc.CTMCBlueprint;
import org.twz.cx.multimodel.ModelLayout;
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
    private Map<String, IStateSpaceBlueprint> DCores;
    private Map<String, IModelBlueprint> MCores;
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

    public BayesNet createBayesNet(String name) {
        assert !BNs.containsKey(name);
        BayesNet BN = new BayesNet(name);
        addBayesNet(BN);
        return BN;
    }

    public void listBayesNets() {
        System.out.println(BNs.keySet());
    }

    public BayesNet getBayesNet(String name) {
        return BNs.get(name);
    }

    public void addStateSpace(IStateSpaceBlueprint dc) {
        if(DCores.putIfAbsent(dc.getName(), dc)!=null) {
            Log.info("New State space dynamic model " + dc.getName() + " added");
        }
    }

    public void loadStateSpace(String path) {
        if (path.endsWith(".json")) {
            readStateSpace(IO.loadJSON(path));
        } else {
            readStateSpace(IO.loadText(path));
        }
    }

    public void readStateSpace(String script) {
        try {
            addStateSpace(StateSpaceFactory.createFromScripts(script));
        } catch (ScriptException e) {
            Log.warning("Invalidated script");
        }
    }

    public void readStateSpace(JSONObject js) {
        addStateSpace(StateSpaceFactory.createFromJSON(js));
    }

    public void listStateSpace() {
        System.out.println(DCores.keySet().toString());
    }

    public IStateSpaceBlueprint getStateSpace(String name) {
        return DCores.get(name);
    }

    public IStateSpaceBlueprint createStateSpace(String name, String type) {
        assert !DCores.containsKey(name);
        IStateSpaceBlueprint ss;
        switch (type) {
            case "CTBN":
                ss = new CTBNBlueprint(name);
                break;
            case "CTMC":
                ss = new CTMCBlueprint(name);
                break;
            default:
                Log.warning("Unknown type of state space");
                return null;
        }

        addStateSpace(ss);
        return ss;
    }

    public void addSimModel(IModelBlueprint mc) {
        MCores.putIfAbsent(mc.getName(), mc);
    }

    public void loadSimModel(String path) {
        restoreSimModel(IO.loadJSON(path));
    }

    public void restoreSimModel(JSONObject js) {
        //try {
        //    addDCore(name, MCoreBuilder.buildFromJSON(js));
        //} catch (ScriptException e) {
        //    e.printStackTrace();
        //}
        // todo
    }

    public void listSimModels() {
        System.out.println(MCores.keySet().toString());
    }

    public IModelBlueprint getSimModel(String name) {
        return MCores.get(name);
    }

    public IModelBlueprint createSimModel(String name, String type) {
        assert !MCores.containsKey(name);
        IModelBlueprint mbp;
        switch (type) {
            case "StSpABM":
                mbp = new StSpABMBlueprint(name);
                break;
            case "ODEEBM":
                mbp = new ODEEBMBlueprint(name);
                break;
            default:
                Log.warning("Unknown type of simulation model");
                return null;
        }

        addSimModel(mbp);
        return mbp;
    }

    public void addModelLayout(ModelLayout ml) {
        Layouts.putIfAbsent(ml.getName(), ml);
    }

    public ModelLayout createModelLayout(String name) {
        assert !Layouts.containsKey(name);
        ModelLayout layout = new ModelLayout(name);
        addModelLayout(layout);
        return layout;
    }

    public NodeGroup getParameterHierarchy(String name) {
        assert Layouts.containsKey(name);
        return Layouts.get(name).getParameterHierarchy(this);
    }

    public ParameterCore generatePCore(String name, String bn) {
        return getBayesNet(bn).toSimulationCore().generate(name);
    }

    public AbsStateSpace generateDCore(String dc, String pc) {
        return generateDCore(dc, generatePCore(dc, pc));
    }

    public AbsStateSpace generateDCore(String dc, ParameterCore pc) {
        IStateSpaceBlueprint bp = getStateSpace(dc);
        if (bp.isCompatible(pc)) {
            return bp.generateModel(pc);
        } else {
            return null;
        }
    }

    public AbsSimModel generateMCore(String name, String type, String bn) {
        Map<String, Object> args = new HashMap<>();
        args.put("bn", bn);
        args.put("da", this);
        return MCores.get(type).generate(name, args);
    }

    public AbsSimModel generateMCore(String name, String type, ParameterCore pc) {
        Map<String, Object> args = new HashMap<>();
        args.put("pc", pc);
        args.put("da", this);
        return MCores.get(type).generate(name, args);
    }

    public AbsSimModel generateModel(String name, String bn) {
        assert Layouts.containsKey(name);
        ModelLayout layout = Layouts.get(name);
        NodeGroup ng = layout.getParameterHierarchy(this);
        ParameterCore pc = getBayesNet(bn).toSimulationCore(ng, true).generate("Test");
        return layout.generate(this, pc);
    }

    public IY0 generateModelY0(String name) {
        assert Layouts.containsKey(name);
        return Layouts.get(name).getY0s();
    }
}
