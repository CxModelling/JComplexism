package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.Director;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.mcore.IModelBlueprint;
import org.twz.cx.mcore.FnSummary;
import org.twz.dag.ParameterModel;
import org.twz.dag.Parameters;
import org.twz.dag.NodeSet;
import org.twz.statespace.AbsStateSpace;
import org.twz.statespace.IStateSpaceBlueprint;

import java.util.*;

public class StSpABMBlueprint implements IModelBlueprint<StSpABModel> {
    private class PopEntry {
        String Prefix, Group, Dynamic;
        String[] Attributes;
        Map<String, Object> Arg;

        PopEntry(String prefix, String group, String[] atr, String dc, Map<String, Object> arg) {
            Prefix = prefix;
            Group = group;
            Dynamic = dc;
            Attributes = atr;
            Arg = arg;
        }
    }


    private final String Name;
    private String TimeKey;
    private PopEntry Population;
    private String[] FixVars, FloatVars;
    private List<JSONObject> Networks, Behaviours;
    private List<String> ObsBehaviours, ObsStates, ObsTransitions;
    private FnSummary Summariser;

    public StSpABMBlueprint(String name) {
        Name = name;
        TimeKey = null;
        Networks = new ArrayList<>();
        Behaviours = new ArrayList<>();
        FixVars = new String[0];
        FloatVars = new String[0];
    }

    public void setAgent(String prefix, String group, String[] atr, String dc, Map<String, Object> arg) {
        Population = new PopEntry(prefix, group, atr, dc, arg);
    }

    public void setAgent(String prefix, String group, String dc, Map<String, Object> arg) {
        setAgent(prefix, group, new String[0], dc, arg);
    }

    public void setAgent(String prefix, String group, String dc, String[] atr) {
        setAgent(prefix, group, atr, dc, new HashMap<>());
    }

    public void setAgent(String prefix, String group, String dc) {
        setAgent(prefix, group, new String[0], dc, new HashMap<>());
    }

    public void setPars(String[] fx, String[] fl) {
        FixVars = fx;
        FloatVars = fl;
    }

    public void addNetwork(String name, String type, Map<String, Object> arg) throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", name);
        js.put("Type", type);
        js.put("Args", arg);
        addNetwork(js);
    }

    public void addNetwork(JSONObject js) {
        Networks.add(js);
    }

    public void addBehaviour(String name, String type, Map<String, Object> arg) throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", name);
        js.put("Type", type);
        js.put("Args", arg);
        addBehaviour(js);
    }

    public void addBehaviour(String js) throws JSONException {
        addBehaviour(new JSONObject(js));
    }

    public void addBehaviour(JSONObject js) {
        Behaviours.add(js);
    }

    public void setObservations(String[] states, String[] transitions, String[] behaviours) {
        try {
            ObsStates = Arrays.asList(states);
        } catch (NullPointerException ignored) {

        }

        try {
            ObsTransitions = Arrays.asList(transitions);
        } catch (NullPointerException ignored) {

        }

        try {
            ObsBehaviours = Arrays.asList(behaviours);
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public void setOption(String opt, Object value) {

    }

    @Override
    public void setTimeKey(String k) {
        TimeKey = k;
    }

    @Override
    public void setSummariser(FnSummary summariser) {
        Summariser = summariser;
    }

    @Override
    public NodeSet getParameterHierarchy(Director da) {
        NodeSet ns = new NodeSet(Name, FixVars, FloatVars);
        assert Population != null;

        IStateSpaceBlueprint dc = da.getStateSpace(Population.Dynamic);
        assert dc != null;
        String[] needs = dc.getRequiredDistributions();
        ns.appendChild(new NodeSet(Population.Group, Population.Attributes, needs));
        return ns;
    }

    @Override
    public StSpABModel generate(String name, Map<String, Object> args) {
        Parameters pc;
        AbsStateSpace dc;

        if (args.containsKey("bn") && args.containsKey("da")) {
            Director da = (Director) args.get("da");
            IStateSpaceBlueprint dbp = da.getStateSpace(Population.Dynamic);
            ParameterModel pm = da.getBayesNet((String) args.get("bn")).toParameterModel(getParameterHierarchy(da));
            if (args.containsKey("exo")) {
                pc = pm.generate(name, (Map<String, Double>) args.get("exo"));
            } else {
                pc = pm.generate(name);
            }

            dc = dbp.generateModel(pc.genPrototype(Population.Group));

        } else if(args.containsKey("pc")) {
            pc = (Parameters) args.get("pc");
            if (args.containsKey("dc")) {
                dc = (AbsStateSpace) args.get("dc");
            } else {
                Director da = (Director) args.get("da");
                IStateSpaceBlueprint dbp = da.getStateSpace(Population.Dynamic);
                dc = dbp.generateModel(pc.genPrototype(Population.Group));
            }
        } else {
            return null;
        }


        StSpPopulation pop = new StSpPopulation(Population.Prefix, Population.Group, dc, pc);
        StSpABModel model = new StSpABModel(name, pc, pop);

        StSpBehaviourFactory.appendResource("States", dc.getStateSpace());
        StSpBehaviourFactory.appendResource("Transitions", dc.getTransitionSpace());
        StSpBehaviourFactory.appendResource("Samplers", pc.getSamplers());
        AbsBehaviour be;
        for (JSONObject behaviour : Behaviours) {
            be = StSpBehaviourFactory.create(behaviour);
            // be.setParameters(pc.breed(be.getName(), be.getName()));
            model.addBehaviour(be);
        }
        StSpBehaviourFactory.clearResource();

        ObsStates.forEach(model::addObservingState);
        ObsTransitions.forEach(model::addObservingTransition);
        ObsBehaviours.forEach(model::addObservingBehaviour);

        if (Summariser != null) {
            model.setSummariser(Summariser);
        } else {
            model.setSummariser((tab, model1, ti) -> {});
        }

        if (TimeKey != null) model.setTimeKey(TimeKey);

        return model;
    }

    @Override
    public boolean isWellDefined() {
        return Population != null && ObsStates.size() + ObsTransitions.size() + ObsBehaviours.size() > 0;
    }
}
