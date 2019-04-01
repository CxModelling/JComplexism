package org.twz.dag;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.actor.CompoundActor;
import org.twz.dag.actor.FrozenSingleActor;
import org.twz.dag.actor.Sampler;
import org.twz.dag.actor.SimulationActor;
import org.twz.dag.loci.Loci;
import org.twz.graph.DiGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public class ParameterCore extends Chromosome {
    public static ParameterCore NullParameters = new ParameterCore("Null", null, new HashMap<>(), 0);

    private final String Nickname;
    private SimulationGroup SG;
    private ParameterCore Parent;
    Map<String, SimulationActor> Actors;
    private Map<String, ParameterCore> Children;
    Map<String, Map<String, SimulationActor>> ChildrenActors;
    private boolean Frozen;

    public ParameterCore(String nickname, SimulationGroup sg, Map<String, Double> fixed, double prior) {
        super(fixed, prior);
        Nickname = nickname;
        SG = sg;
        Children = new HashMap<>();
        Actors = new HashMap<>();
        ChildrenActors = new HashMap<>();
        Frozen = false;
    }

    public void setParent(ParameterCore parent) {
        Parent = parent;
    }

    public String getName() {
        return Nickname;
    }

    public String getGroupName() {
        return SG.getName();
    }

    @Override
    public double getDouble(String s) {
        if (has(s)) {
            return super.getDouble(s);
        } else {
            return Parent.getDouble(s);
        }
    }

    public ParameterCore breed(String nickname, String group, Map<String, Double> exo) {
        if (Children.containsKey(nickname)) {
            return Children.get(nickname);
        } else {
            ParameterCore chd = SG.breed(nickname, group, exo, this);
            Children.put(nickname, chd);
            return chd;
        }
    }

    public ParameterCore breed(String nickname, String group) {
        return breed(nickname, group, null);
    }

    public ParameterCore genSibling(String nickname, Map<String, Double> exo) {
        if (Parent != null) {
            return Parent.breed(nickname, getGroupName(), exo);
        } else {
            return SG.generate(nickname, exo, null, true);
        }

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
        Actors.putAll(Parent.ChildrenActors.get(getGroupName()));

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
        if (Actors.containsKey(sampler)) {
            return new Sampler(Actors.get(sampler), this);
        } else {
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
        resetProbability();
        SG.setResponse(imp, shock_l, shock_a, shock_h, this);

        Children.values().forEach(ch->ch.setResponse(imp, shocked));
    }

    void freeze() {
        if (Frozen) {
            return;
        } else {
            Frozen = true;
        }

        for (SimulationActor act : Actors.values()) {
            if (act instanceof CompoundActor) {
                ((CompoundActor) act).fillAll(this);
            } else {
                this.put(act.Field, act.sample(this));
            }
        }

        if (Parent != null) {
            for (SimulationActor act : Parent.ChildrenActors.get(getGroupName()).values()) {
                if (act instanceof CompoundActor) {
                    ((CompoundActor) act).fillAll(this);
                } else {
                    this.put(act.Field, act.sample(this));
                }
            }
        }
    }

    void resetSC(SimulationCore sc) {
        SG = sc.get(SG.getName());
        Children.values().forEach(ch->ch.resetSC(sc));
    }

    public double getDeepLogPrior() {
        return getLogPriorProb() + Children.values().stream().mapToDouble(ParameterCore::getDeepLogPrior).sum();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Name", Nickname);
        return js;
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
        ParameterCore pc = new ParameterCore(Nickname, SG, getLocus(), getLogPriorProb());
        if (isLikelihoodEvaluated()) pc.setLogLikelihood(getLogLikelihood());
        return pc;
    }


}
