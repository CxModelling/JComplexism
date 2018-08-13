package org.twz.cx.abmodel.statespace;

import org.json.JSONObject;
import org.twz.cx.mcore.IMCoreBlueprint;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.AbsDCore;
import org.twz.statespace.IBlueprintDCore;

import java.util.*;

public class StSpABMBlueprint implements IMCoreBlueprint<StSpABModel> {
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
    public NodeGroup getParameterHierarchy(IBlueprintDCore dc) {
        NodeGroup ng = new NodeGroup(Name, new String[0]);
        assert Population != null;
        ng.appendChildren(new NodeGroup(Population.Group, dc.getRequiredDistributions()));
        return ng;
    }

    @Override
    public StSpABModel generate(String name, Map<String, Object> args) {
        ParameterCore pc = (ParameterCore) args.get("pc");
        AbsDCore dc = (AbsDCore) args.get("dc");

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
    public StSpABModel generate(String name) {
        return null;
    }
}