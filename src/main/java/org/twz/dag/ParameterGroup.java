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
    private Set<String> Listening, BeFixed, BeFloating;
    private List<Loci> FixedChain;
    Set<String> Children;
    private List<ActorBlueprint> Bps;

    private Map<String, List<String>> AffectedFixed, AffectedFloating;
    private Map<String, Map<String, List<String>>> AffectedChildren;

    ParameterGroup(String name, Set<String> listening, Set<String> fixed, Set<String> random) {
        Name = name;
        Listening = listening;
        BeFixed = fixed;
        BeFloating = random;
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
    }

    private List<ActorBlueprint> getActorBlueprints() {
        if (Bps == null) {
            Bps = new ArrayList<>();
            BayesNet bn = PM.getBN();
            DiGraph<Loci> g = bn.getDAG();

            List<String> order = bn.getOrder(), flow;

            for (String act : BeFloating) {
                Set<String> pa = new HashSet<>(g.getParents(act));

                if (hasIntersection(pa, BeFloating)) {
                    flow = new ArrayList<>(order);
                    flow.retainAll(g.getAncestors(act));
                    flow.retainAll(BeFloating);
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Compound, flow));
                } else if (hasIntersection(pa, Listening)) {
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Single));
                } else if (hasIntersection(pa, BeFixed)) {
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Single));
                } else {
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Frozen));
                }
            }
        }
        return Bps;
    }

    private Map<String, SimulationActor> getActors(Chromosome pas) {
        BayesNet bn = PM.getBN();
        Map<String, SimulationActor> res = new HashMap<>();
        SimulationActor actor;

        for (ActorBlueprint act : getActorBlueprints()) {
            String name = act.Actor;
            switch (act.Type) {
                case ActorBlueprint.Frozen:
                    try {
                        actor = new FrozenSingleActor(name, bn.getLoci(name), pas);
                    } catch (ClassCastException e) {
                        continue;
                    }
                    break;
                case ActorBlueprint.Single:
                    actor = new SingleActor(name, bn.getLoci(name));
                    break;
                default:
                    actor = new CompoundActor(name,
                            act.Flow.stream().map(bn::getLoci).collect(Collectors.toList()), bn.getLoci(name));
                    break;
            }
            res.put(name, actor);
        }
        return res;
    }

    private boolean hasIntersection(Set<String> a, Set<String> b) {
        Set<String> temp = new HashSet<>(a);
        temp.retainAll(b);
        return !temp.isEmpty();
    }

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

        if (parent == null) {
            pc.Actors = getActors(pc);
        }

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
                FrozenSingleActor act = (FrozenSingleActor) ps.Actors.get(actor);
                act.update(ps);
            } catch (NullPointerException | ClassCastException | IncompleteConditionException ignored) {

            }
        }

        Map<String, SimulationActor> chd;

        for (String s : Children) {
            shocked = new HashSet<>();
            for (String k : imp.keySet()) {
                shocked.addAll(getAffectedChildren(k, s));
            }

            chd = ps.ChildrenActors.get(s);
            for (String actor : shocked) {
                try {
                    FrozenSingleActor act = (FrozenSingleActor) chd.get(actor);
                    act.update(ps);
                } catch (ClassCastException | IncompleteConditionException ignored) {

                }
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

            if (!parent.ChildrenActors.containsKey(group)) {
                parent.ChildrenActors.put(group, ch_pg.getActors(ch));
            }
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
