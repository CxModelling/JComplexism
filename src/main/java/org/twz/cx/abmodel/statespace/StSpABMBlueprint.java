package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.twz.cx.Director;
import org.twz.cx.mcore.IModelBlueprint;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsStateSpace;
import org.twz.statespace.IStateSpaceBlueprint;

import java.util.*;

public class StSpABMBlueprint implements IModelBlueprint<StSpABModel> {
    private class PopEntry {
        String Prefix, Group, Dynamic;
        Map<String, Double> Exo;
        Map<String, Object> Arg;

        PopEntry(String prefix, String group, Map<String, Double> exo, String dc, Map<String, Object> arg) {
            Prefix = prefix;
            Group = group;
            Dynamic = dc;
            Exo = exo;
            Arg = arg;
        }
    }

    private final String Name;
    private PopEntry Population;
    private List<JSONObject> Networks, Behaviours;
    private List<String> ObsBehaviours, ObsStates, ObsTransitions;

    public StSpABMBlueprint(String name) {
        Name = name;
        Networks = new ArrayList<>();
        Behaviours = new ArrayList<>();
    }

    public void setAgent(String prefix, String group, Map<String, Double> exo, String dc, Map<String, Object> arg) {
        Population = new PopEntry(prefix, group, exo, dc, arg);
    }

    public void setAgent(String prefix, String group, String dc, Map<String, Object> arg) {
        setAgent(prefix, group, new HashMap<>(), dc, arg);
    }

    public void setAgent(String prefix, String group, String dc) {
        setAgent(prefix, group, new HashMap<>(), dc, new HashMap<>());
    }

    public void addNetwork(String name, String type, Map<String, Object> arg) {
        JSONObject js = new JSONObject();
        js.put("Name", name);
        js.put("Type", type);
        js.put("Args", arg);
        addNetwork(js);
    }

    public void addNetwork(JSONObject js) {
        Networks.add(js);
    }

    public void addBehaviour(String name, String type, Map<String, Object> arg) {
        JSONObject js = new JSONObject();
        js.put("Name", name);
        js.put("Type", type);
        js.put("Args", arg);
        addBehaviour(js);
    }

    public void addBehaviour(String js) {
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
    public NodeGroup getParameterHierarchy(IStateSpaceBlueprint dc) {
        NodeGroup ng = new NodeGroup(Name, new String[0]);
        assert Population != null;
        String[] needs = dc.getRequiredDistributions();
        ng.appendChildren(new NodeGroup(Population.Group, needs));
        return ng;
    }

    @Override
    public StSpABModel generate(String name, Map<String, Object> args) {
        ParameterCore pc;
        AbsStateSpace dc;

        if (args.containsKey("bn") && args.containsKey("da")) {
            Director da = (Director) args.get("da");
            IStateSpaceBlueprint dbp = da.getStateSpace(Population.Dynamic);
            pc = da.getBayesNet((String) args.get("bn")).toSimulationCore(getParameterHierarchy(dbp), true).generate(name);
            dc = dbp.generateModel(pc.genPrototype(Population.Group));

        } else if(args.containsKey("pc")) {
            pc = (ParameterCore) args.get("pc");
            if (args.containsKey("dc")) {
                dc = (AbsStateSpace) args.get("dc");
            } else {
                Director da = (Director) args.get("da");
                IStateSpaceBlueprint dbp = da.getStateSpace(Population.Dynamic);
                dc = dbp.generateModel(pc);
            }
        } else {
            return null;
        }


        StSpPopulation pop = new StSpPopulation(Population.Prefix, Population.Group, dc, pc, Population.Exo);
        StSpABModel model = new StSpABModel(name, pc, pop);

        StSpBehaviourFactory.appendResource("States", dc.getStateSpace());
        StSpBehaviourFactory.appendResource("Transitions", dc.getTransitionSpace());
        StSpBehaviourFactory.appendResource("Samplers", pc.getSamplers());
        for (JSONObject behaviour : Behaviours) {
            model.addBehaviour(StSpBehaviourFactory.create(behaviour));
        }
        StSpBehaviourFactory.clearResource();

        ObsStates.forEach(model::addObservingState);
        ObsTransitions.forEach(model::addObservingTransition);
        ObsBehaviours.forEach(model::addObservingBehaviour);

        return model;
    }

    @Override
    public boolean isWellDefined() {
        if (Population == null) return false;
        if (ObsStates.size() + ObsTransitions.size() + ObsBehaviours.size() <= 0) return false;
        return true;
    }
}
