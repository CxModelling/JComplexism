package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.actor.*;

import org.twz.dag.loci.ExoValueLoci;
import org.twz.dag.loci.FunctionLoci;
import org.twz.dag.loci.Loci;
import org.twz.exception.IncompleteConditionException;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.stream.Collectors;


public class ParameterGroup implements AdapterJSONObject {
    private final String Name;
    private ParameterModel PM;
    private NodeSet Structure;
    private Set<String> Listening, BeFixed, BeFloating;
    private List<Loci> FixedChain;
    Set<String> Children;

    private Map<String, ActorBlueprint> ActorBlueprints;
    private Map<String, SimulationActor> Actors;

    private Map<String, List<String>> AffectedFixed, AffectedFloating;
    private Map<String, Map<String, List<String>>> AffectedChildren;

    ParameterGroup(NodeSet ns) {
        Name = ns.getName();
        Structure = ns;
        Listening = ns.getExoNodes();
        BeFixed = ns.getFixedNodes();
        BeFloating = ns.getFloatingNodes();

        ActorBlueprints = ns.getFloatingBlueprints();

        Children = new HashSet<>();

        AffectedFixed = new HashMap<>();
        AffectedFloating = new HashMap<>();
        AffectedChildren = new HashMap<>();
    }

    public String getName() {
        return Name;
    }

    Set<String> getListening() {
        return Listening;
    }

    void setParameterModel(ParameterModel pm) {
        PM = pm;
        FixedChain = PM.getBN().getOrder().stream()
                .filter(d->BeFixed.contains(d))
                .map(d->PM.getBN().getLoci(d))
                .collect(Collectors.toList());
        findActors();
    }

    private void findActors() {
        Actors = new HashMap<>();
        BayesNet bn = PM.getBN();
        SimulationActor actor;

        for (Map.Entry<String, ActorBlueprint> ent : ActorBlueprints.entrySet()) {
            String name = ent.getKey();
            ActorBlueprint act = ent.getValue();
            switch (act.Type) {
                case ActorBlueprint.Single:
                    actor = new SingleActor(name, bn.getLoci(name));
                    Actors.put(name, actor);
                    break;
                case ActorBlueprint.Compound:
                    actor = new CompoundActor(name,
                            act.Flow.stream().map(bn::getLoci).collect(Collectors.toList()), bn.getLoci(name));
                    Actors.put(name, actor);
                    break;
            }

        }
    }


    SimulationActor getActor(String act, Chromosome pars) {
        if (!ActorBlueprints.containsKey(act)) {
            return null;
        }
        if (Actors.containsKey(act)) {
            return Actors.get(act);
        } else {
            return new FrozenSingleActor(act, PM.getBN().getLoci(act), pars);
        }
    }

    List<String> getActorList() {
        return new ArrayList<>(ActorBlueprints.keySet());
    }

    Set<String> getAvailableFixed() { return Structure.getAvailableFixed(); }

    Parameters generate(String nickname, Map<String, Double> exo, Parameters parent) {
        Parameters pc = new Parameters(nickname, this, exo, 0);
        pc.setParent(parent);

        FixedChain.stream()
                .filter(loci -> !(loci instanceof ExoValueLoci))
                .filter(loci -> !pc.has(loci.getName()))
                .forEach(loci -> {
                    try {
                        loci.fill(pc);
                    } catch (IncompleteConditionException ignored) {
                    }
                });

        PM.getBN().evaluate(pc);
        return pc;
    }

    private List<String> getAffectedFixed(String imp) {
        if (!AffectedFixed.containsKey(imp)) {
            findAffected(imp);
        }
        return AffectedFixed.get(imp);
    }

    List<String> getAffectedFloating(String imp) {
        if (!AffectedFloating.containsKey(imp)) {
            findAffected(imp);
        }
        return AffectedFloating.get(imp);
    }

    private List<String> getAffectedChildren(String imp, String group) {
        if (!AffectedChildren.containsKey(group)) {
            AffectedChildren.put(group, new HashMap<>());
        }
        if (!AffectedChildren.get(group).containsKey(imp)) {
            AffectedChildren.get(group).put(imp, PM.get(group).getAffectedFloating(imp));
        }
        return AffectedChildren.get(group).get(imp);
    }

    private void findAffected(String imp) {
        DiGraph<Loci> dag = PM.getBN().getDAG();

        Set<String> des = new HashSet<>(), temp;
        List<String> querying = dag.getChildNodes(imp).stream()
                .filter(this::canBeShocked)
                .map(Loci::getName)
                .collect(Collectors.toList());

        while(!querying.isEmpty()) {
            des.addAll(querying);
            temp = new HashSet<>();

            for (String l : querying) {
                temp.addAll(dag.getChildNodes(l).stream()
                        .filter(this::canBeShocked)
                        .map(Loci::getName)
                        .collect(Collectors.toList()));
            }
            querying = temp.stream().filter(e->!des.contains(e)).collect(Collectors.toList());
        }

        List<String> affected = new ArrayList<>(des);
        affected.retainAll(BeFixed);
        AffectedFixed.put(imp, affected);

        affected = new ArrayList<>(des);
        affected.retainAll(BeFloating);
        AffectedFloating.put(imp, affected);
    }

    private boolean canBeShocked(Loci loci) {
        if (FixedChain.contains(loci)) {
            return loci instanceof FunctionLoci;
        }
        return BeFloating.contains(loci.getName());
    }

    void setResponse(Map<String, Double> imp, Parameters ps) {
        Set<String> shocked = new HashSet<>();

        // check shocked fixed nodes
        for (String k : imp.keySet()) {
            shocked.addAll(getAffectedFixed(k));
        }
        shocked.removeAll(imp.keySet());
        for (Map.Entry<String, Double> ent : imp.entrySet()) {
            if (Double.isNaN(ent.getValue()))
                shocked.add(ent.getKey());
        }
        if (!shocked.isEmpty()) ps.resetProbability();

        // update fixed nodes
        for (Loci loci : FixedChain) {
            try {
                if (shocked.contains(loci.getName())) {
                    loci.fill(ps);
                } else if (imp.containsKey(loci.getName())) {
                    ps.put(loci.getName(), imp.get(loci.getName()));
                    ps.addLogPriorProb(loci.evaluate(ps));
                } else {
                    ps.addLogPriorProb(loci.evaluate(ps));
                }
            } catch (IncompleteConditionException ignored) {}
        }

        // update frozen actors
        shocked = new HashSet<>();
        for (String k : imp.keySet()) {
            shocked.addAll(getAffectedFloating(k));
        }

        for (String actor : shocked) {
            try {
                ps.Samplers.get(actor).update();
            } catch (NullPointerException | ClassCastException ignored) {

            }
        }

        for (String s : Children) {
            shocked = new HashSet<>();
            for (String k : imp.keySet()) {
                shocked.addAll(getAffectedChildren(k, s));
            }
        }
    }

    Parameters breed(String nickname, String group, Map<String, Double> exo, Parameters parent) {

        ParameterGroup ch_pg = PM.get(group);

        Parameters ch;
        if (ch_pg == null) {
            ch = new PseudoParameters(nickname);
            ch.setParent(parent);
        } else {
            ch = ch_pg.generate(nickname, exo, parent);

            //if (!parent.ChildrenActors.containsKey(group)) {
            //    parent.ChildrenActors.put(group, ch_pg.getActors(ch));
            //}
        }

        return ch;
    }

    public void freeze(Parameters parameters, String loci) {
        if (Listening.contains(loci)) {
            parameters.put(loci, parameters.getDouble(loci));
        } else if (BeFloating.contains(loci)) {
            parameters.put(loci, parameters.getSampler(loci).sample());
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("Listening", new JSONArray(Listening));
        js.put("BeFixed", new JSONArray(BeFixed));
        js.put("BeFloating", new JSONArray(BeFloating));
        js.put("Children", new JSONArray(Children));
        return js;
    }

}
