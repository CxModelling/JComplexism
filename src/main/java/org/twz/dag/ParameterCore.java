package org.twz.dag;

import org.twz.dag.actor.FrozenSingleActor;
import org.twz.dag.actor.Sampler;
import org.twz.dag.actor.SimulationActor;
import org.twz.dag.loci.Loci;
import org.twz.graph.DiGraph;
import org.twz.prob.IDistribution;
import org.twz.prob.Sample;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public class ParameterCore extends Gene {

    private final String Nickname;
    private SimulationGroup SG;
    private ParameterCore Parent;
    Map<String, SimulationActor> Actors;
    private Map<String, ParameterCore> Children;
    Map<String, Map<String, SimulationActor>> ChildrenActors;


    public ParameterCore(String nickname, SimulationGroup sg, Map<String, Double> fixed, double prior) {
        super(fixed, prior);
        Nickname = nickname;
        SG = sg;
        Children = new HashMap<>();
        Actors = new HashMap<>();
        ChildrenActors = new HashMap<>();
    }

    public void setParent(ParameterCore parent) {
        Parent = parent;
    }

    public String getGroupName() {
        return SG.getName();
    }

    @Override
    public double get(String s) {
        try {
            return super.get(s);
        } catch (NullPointerException e) {
            return Parent.get(s);
        }
    }

    public ParameterCore breed(String nickname, String group, Map<String, Double> exo) {
        try {
            return Children.get(nickname);
        } catch (IndexOutOfBoundsException e) {
            ParameterCore chd = SG.breed(nickname, group, exo, this);
            Children.put(nickname, chd);
            return chd;
        }
    }

    public ParameterCore breed(String nickname, String group) {
        return breed(nickname, group, null);
    }

    public ParameterCore genSibling(String nickname, Map<String, Double> exo) {
        return Parent.breed(nickname, getGroupName(), exo);
    }

    public ParameterCore genSibling(String nickname) {
        return Parent.breed(nickname, getGroupName());
    }

    public ParameterCore genPrototype(String group, Map<String, Double> exo) {
        return SG.breed("Proto", group, exo, this);
    }

    public ParameterCore genPrototype(String group) {
        return SG.breed("Proto", group, null, this);
    }

    public void detachFromParent(boolean collect) {
        if (Parent == null) {
            return;
        }
        Parent.removeChild(Nickname);
        if (collect) {
            getLocus().putAll(Parent.getLocus());
        }
        Parent = null;
    }

    public void detachFromParent() {
        detachFromParent(false);
    }

    public ParameterCore removeChild(String k) {
        return Children.remove(k);
    }

    public List<String> listSamplers() {
        List<String> li = new ArrayList<>(Actors.keySet());
        if (Parent != null) {
            li.addAll(Parent.ChildrenActors.get(getGroupName()).keySet());
        }
        return li;
    }

    public Map<String, Sampler> getSamplers() {
        Map<String, Sampler> li = new HashMap<>();
        for (Map.Entry<String, SimulationActor> entry : Actors.entrySet()) {
            li.put(entry.getKey(), new Sampler(entry.getValue(), this));
        }

        if (Parent != null) {
            for (Map.Entry<String, SimulationActor> entry : Parent.ChildrenActors.get(getGroupName()).entrySet()) {
                li.put(entry.getKey(), new Sampler(entry.getValue(), this));
            }
        }
        return li;
    }

    public Sampler getSampler(String sampler) {
        try {
            return new Sampler(Actors.get(sampler), this);
        } catch (IndexOutOfBoundsException e) {
            return new Sampler(Parent.ChildrenActors.get(getGroupName()).get(sampler), this);
        }
    }

    public ParameterCore getChild(String chd) {
        return Children.get(chd);
    }

    public SimulationActor getChildActor(String group, String name) {
        return ChildrenActors.get(group).get(name);
    }

    public void impulse(Map<String, Double> imp) {
        DiGraph<Loci> g = SG.getSC().getBN().getDAG();
        Set<String> shocked = new HashSet<>();

        for (String s : imp.keySet()) {
            shocked.addAll(g.getDescendants(s));
        }

        shocked.removeAll(imp.keySet());
        setResponse(imp, shocked);
    }

    private void setResponse(Map<String, Double> imp, Set<String> shocked) {
        List<String> shock_l = shocked.stream().filter(this::has).collect(Collectors.toList()),
                shock_a = Actors.entrySet().stream()
                        .filter(e->shocked.contains(e.getKey()) && e.getValue() instanceof FrozenSingleActor)
                        .map(Map.Entry::getKey).collect(Collectors.toList());

        Map<String, List<String>> shock_h = new HashMap<>();

        for (Map.Entry<String, Map<String, SimulationActor>> entry : ChildrenActors.entrySet()) {
            shock_h.put(entry.getKey(), entry.getValue().entrySet().stream()
                    .filter(e->shocked.contains(e.getKey()) && e.getValue() instanceof FrozenSingleActor)
                    .map(Map.Entry::getKey).collect(Collectors.toList()));
        }


        SG.setResponse(imp, shock_l, shock_a, shock_h, this);

        Children.values().forEach(ch->ch.setResponse(imp, shocked));
    }

    private void freeze() {
        for (SimulationActor act : Actors.values()) {
            act.fill(this);
        }

        if (Parent != null) {
            for (SimulationActor act : Parent.ChildrenActors.get(getGroupName()).values()) {
                act.fill(this);
            }
        }
    }

    public void resetSC(SimulationCore sc) {
        SG = sc.get(SG.getName());
        Children.values().forEach(ch->ch.resetSC(sc));
    }

    public double getDeepLogPrior() {
        return getLogPriorProb() + Children.values().stream()
                .mapToDouble(ParameterCore::getDeepLogPrior).sum();
    }

    public String toJSON() {
        String sb = "{";
        sb += getLocus().entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(","));
        sb += "}";
        return sb;
    }

    private void deepPrint(int ind) {
        String prefix = new String(new char[ind]).replace("\0", "  ");
        System.out.println(prefix + Nickname + "(" + toString() + ")");
        Children.values().forEach(ch->ch.deepPrint(ind+1));
    }

    public void deepPrint() {
        deepPrint(0);
    }

    public ParameterCore clone() {
        return (ParameterCore) super.clone();
    }


}
