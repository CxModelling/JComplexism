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


public class ParameterGroup implements AdapterJSONObject {
    private final String Name;
    private ParameterModel PM;
    private Set<String> Listening, BeFixed, BeFloating;
    private List<Loci> FixedChain;
    Set<String> Children;
    private List<ActorBlueprint> Bps;

    ParameterGroup(String name, Set<String> listening, Set<String> fixed, Set<String> random) {
        Name = name;
        Listening = listening;
        BeFixed = fixed;
        BeFloating = random;
        Children = new HashSet<>();
    }

    public String getName() {
        return Name;
    }

    BayesNet getBN() {
        return PM.getBN();
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
                    Bps.add(new ActorBlueprint(act, ActorBlueprint.Compound, ActorBlueprint.Compound, flow));
                } else if (hasIntersection(pa, Listening)) {
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

    private Map<String, SimulationActor> getActors(Chromosome pas) {
        BayesNet bn = PM.getBN();
        Map<String, SimulationActor> res = new HashMap<>();
        SimulationActor actor;

        for (ActorBlueprint act : getActorBlueprints()) {
            String name = act.Actor;
            switch (act.TypeH) {
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

        FixedChain.stream().filter(loci -> !(loci instanceof ExoValueLoci)).forEach(loci -> {
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

    void setResponse(Map<String, Double> imp, List<String> fixed, List<String> actors,
                     Map<String, List<String>> hoist, Parameters ps) {
        ps.resetProbability();
        for (Loci loci : FixedChain) {
            try {
                if (imp.containsKey(loci.getName())) {
                    ps.put(loci.getName(), imp.get(loci.getName()));
                    ps.addLogPriorProb(loci.evaluate(ps));
                } else if (fixed.contains(loci.getName())) {
                    loci.fill(ps);
                }
            } catch (IncompleteConditionException ignored) {}

        }

        // update frozen actors
        for (String actor : actors) {
            try {
                FrozenSingleActor act = (FrozenSingleActor) ps.Actors.get(actor);
                act.update(ps);
            } catch (ClassCastException | IncompleteConditionException ignored) {

            }
        }

        for (Map.Entry<String, List<String>> entry : hoist.entrySet()) {
            for (String s : entry.getValue()) {
                try {
                    FrozenSingleActor act = (FrozenSingleActor) ps.ChildrenActors.get(entry.getKey()).get(s);
                    act.update(ps);
                } catch (ClassCastException | IncompleteConditionException ignored) {

                }
            }
        }
    }

    Map<String, SimulationActor> setChildActors(Parameters pa, String group) {
        if (pa.ChildrenActors.containsKey(group)) {
            return pa.ChildrenActors.get(group);
        } else {
            return pa.ChildrenActors.put(group, PM.get(group).getActors(null));
        }
    }

    Parameters breed(String nickname, String group, Map<String, Double> exo, Parameters parent) {
        ParameterGroup ch_pg = PM.get(group);

        Parameters ch = ch_pg.generate(nickname, exo, parent);

        if (!parent.ChildrenActors.containsKey(group)) {
            parent.ChildrenActors.put(group, ch_pg.getActors(ch));
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
