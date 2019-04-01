package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.actor.*;
import org.twz.dag.loci.ExoValueLoci;
import org.twz.dag.loci.Loci;
import org.twz.exception.IncompleteConditionException;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
class SimulationGroup implements AdapterJSONObject {
    private String Name;
    private SimulationCore SC;
    private Set<String> Listening, Waiting, BeFixed, BeRandom, BeActor;
    private List<Loci> FixedChain;
    Set<String> Children;
    private List<ActorBlueprint> Bps;

    SimulationGroup(String name, Set<String> listening, Set<String> waiting,
                           Set<String> fixed, Set<String> random, Set<String> actor) {
        Name = name;
        Listening = listening;
        Waiting = waiting;
        BeFixed = fixed;
        BeRandom = random;
        BeActor = actor;
        Children = new HashSet<>();
    }

    public String getName() {
        return Name;
    }

    SimulationCore getSC() {
        return SC;
    }

    void setSimulationCore(SimulationCore sc) {
        SC = sc;
        FixedChain = SC.getBN().getOrder().stream()
                .filter(d->BeFixed.contains(d))
                .map(d->SC.getBN().getLoci(d))
                .collect(Collectors.toList());
    }

    private List<ActorBlueprint> getActorBlueprints() {
        if (Bps == null) {
            Bps = new ArrayList<>();
            BayesNet bn = getSC().getBN();
            DiGraph<Loci> g = bn.getDAG();

            List<String> order = bn.getOrder(), flow;
            for (String act : BeActor) {
                Set<String> pa = new HashSet<>(g.getParents(act));
                if (hasIntersection(pa, BeRandom)) {
                    flow = new ArrayList<>(order);
                    flow.retainAll(g.getAncestors(act));
                    flow.retainAll(BeRandom);
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Compound, ActorBlueprint.Compound, flow));
                } else if (hasIntersection(pa, Waiting)) {
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Single, ActorBlueprint.Single, null));
                } else if (hasIntersection(pa, BeFixed)) {
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Frozen, ActorBlueprint.Single, null));
                } else {
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Frozen, ActorBlueprint.Frozen, null));
                }
            }

        }
        return Bps;
    }

    private Map<String, SimulationActor> getActors(Chromosome pas, boolean hoist) {
        BayesNet bn = getSC().getBN();
        Map<String, SimulationActor> res = new HashMap<>();
        SimulationActor actor;

        for (ActorBlueprint act : getActorBlueprints()) {
            String name = act.Actor;
            switch (hoist?act.TypeH:act.Type) {
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

    ParameterCore generate(String nickname, Map<String, Double> exo,
                           ParameterCore parent, boolean actors) {
        ParameterCore pc = new ParameterCore(nickname, this, exo, 0);
        pc.setParent(parent);

        FixedChain.stream().filter(loci -> !(loci instanceof ExoValueLoci)).forEach(loci -> {
            try {
                loci.fill(pc);
            } catch (IncompleteConditionException ignored) {
            }
        });

        if (actors) {
            pc.Actors = getActors(pc, SC.isHoist());
        }
        SC.getBN().evaluate(pc);
        return pc;
    }

    void setResponse(Map<String, Double> imp, List<String> fixed, List<String> actors, Map<String, List<String>> hoist, ParameterCore pc) {
        pc.resetProbability();
        for (Loci loci : FixedChain) {
            try {
                if (imp.containsKey(loci.getName())) {
                    pc.put(loci.getName(), imp.get(loci.getName()));
                    pc.addLogPriorProb(loci.evaluate(pc));
                } else if (fixed.contains(loci.getName())) {
                    loci.fill(pc);
                }
            } catch (IncompleteConditionException ignored) {}

        }

        // update frozen actors
        for (String actor : actors) {
            try {
                FrozenSingleActor act = (FrozenSingleActor) pc.Actors.get(actor);
                act.update(pc);
            } catch (ClassCastException ignored) {

            }
        }

        for (Map.Entry<String, List<String>> entry : hoist.entrySet()) {
            for (String s : entry.getValue()) {
                try {
                    FrozenSingleActor act = (FrozenSingleActor) pc.ChildrenActors.get(entry.getKey()).get(s);
                    act.update(pc);
                } catch (ClassCastException ignored) {

                }
            }
        }
    }

    Map<String, SimulationActor> setChildActors(ParameterCore pa, String group) {
        if (pa.ChildrenActors.containsKey(group)) {
            return pa.ChildrenActors.get(group);
        } else {
            return pa.ChildrenActors.put(group, SC.get(group).getActors(null, true));
        }
    }

    ParameterCore breed(String nickname, String group, Map<String, Double> exo, ParameterCore parent) {
        SimulationGroup ch_sg = SC.get(group);
        boolean hoist = SC.isHoist();
        ParameterCore ch = ch_sg.generate(nickname, exo, parent, !hoist);

        if (hoist) {
            if (!parent.ChildrenActors.containsKey(group)) {
                parent.ChildrenActors.put(group, ch_sg.getActors(ch, true));
            }
        } else {
            ch.Actors = ch_sg.getActors(ch, false);
        }
        return ch;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("Listening", new JSONArray(Listening));
        js.put("BeFixed", new JSONArray(BeFixed));
        js.put("BeRandom", new JSONArray(BeRandom));
        js.put("BeActors", new JSONArray(BeActor));
        js.put("Children", new JSONArray(Children));
        return js;
    }
}
