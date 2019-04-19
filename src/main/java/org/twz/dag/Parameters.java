package org.twz.dag;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.actor.CompoundActor;
import org.twz.dag.actor.FrozenSingleActor;
import org.twz.dag.actor.Sampler;
import org.twz.dag.actor.SimulationActor;
import org.twz.dag.loci.Loci;
import org.twz.exception.IncompleteConditionException;
import org.twz.graph.DiGraph;

import java.util.*;
import java.util.stream.Collectors;

public class Parameters extends Chromosome {

    private final String NickName;
    private ParameterGroup PG;
    private Parameters Parent;
    Map<String, SimulationActor> Actors;
    private Map<String, Parameters> Children;
    Map<String, Map<String, SimulationActor>> ChildrenActors;
    private boolean Frozen;

    Parameters(String nickname, ParameterGroup sg, Map<String, Double> fixed, double prior) {
        super(fixed, prior);
        NickName = nickname;
        PG = sg;
        Children = new HashMap<>();
        Actors = new HashMap<>();
        ChildrenActors = new HashMap<>();
        Frozen = false;
    }

    void setParent(Parameters parent) {
        Parent = parent;
    }

    public String getName() {
        return NickName;
    }

    public String getGroupName() {
        return PG.getName();
    }

    @Override
    public double getDouble(String s) {
        if (has(s)) {
            return super.getDouble(s);
        } else {
            try {
                return Parent.getDouble(s);
            } catch (NullPointerException ex) {
                return getSampler(s).next();
            }
        }
    }


    public Parameters breed(String nickname, String group, Map<String, Double> exo) {
        if (Children.containsKey(nickname)) {
            return Children.get(nickname);
        } else {
            Parameters chd = PG.breed(nickname, group, exo, this);
            Children.put(nickname, chd);
            return chd;
        }
    }

    public Parameters breed(String nickname, String group) {
        return breed(nickname, group, null);
    }

    public Parameters genSibling(String nickname, Map<String, Double> exo) {
        if (Parent != null) {
            return Parent.breed(nickname, getGroupName(), exo);
        } else {
            return PG.generate(nickname, exo, null);
        }

    }

    public Parameters genSibling(String nickname) {
        return Parent.breed(nickname, getGroupName());
    }

    public Parameters genPrototype(String group, Map<String, Double> exo) {
        return PG.breed("Proto", group, exo, this);
    }

    public Parameters genPrototype(String group) {
        return PG.breed("Proto", group, null, this);
    }

    public void detachFromParent(boolean collect) {
        if (Parent == null) {
            return;
        }
        Actors.putAll(Parent.ChildrenActors.get(getGroupName()));

        Parent.removeChild(NickName);
        if (collect) {
            for (String s : PG.getListening()) {
                getLocus().put(s, Parent.getDouble(s));
            }
        }
        Parent = null;
    }

    public void detachFromParent() {
        detachFromParent(false);
    }

    private void removeChild(String k) {
        Children.remove(k);
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

    public Parameters getChild(String chd) {
        return Children.get(chd);
    }

    public SimulationActor getChildActor(String group, String name) {
        return ChildrenActors.get(group).get(name);
    }

    public void impulse(Map<String, Double> imp) {
        DiGraph<Loci> g = PG.getBN().getDAG();
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
        PG.setResponse(imp, shock_l, shock_a, shock_h, this);

        Children.values().forEach(ch->ch.setResponse(imp, shocked));
    }

    private void freeze(String loci) {
        PG.freeze(this, loci);
    }

    void freeze() {
        if (Frozen) {
            return;
        } else {
            Frozen = true;
        }

        for (SimulationActor act : Actors.values()) {
            fillChd(act);
        }

        if (Parent != null) {
            for (SimulationActor act : Parent.ChildrenActors.get(getGroupName()).values()) {
                fillChd(act);
            }
        }
    }

    private void fillChd(SimulationActor act) {
        if (act instanceof CompoundActor) {
            ((CompoundActor) act).fillAll(this);
        } else {
            try {
                this.put(act.Field, act.sample(this));
            } catch (IncompleteConditionException e) {
                e.printStackTrace();
            }
        }
    }

    void resetPM(ParameterModel pm) {
        PG = pm.get(PG.getName());
        Children.values().forEach(ch->ch.resetPM(pm));
    }

    public double getDeepLogPrior() {
        return getLogPriorProb() + Children.values().stream().mapToDouble(Parameters::getDeepLogPrior).sum();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Name", NickName);
        return js;
    }

    private void deepPrint(int ind) {
        String prefix = new String(new char[ind]).replace("\0", "  ");
        System.out.println(prefix + NickName + "(" + toString() + ")");
        Children.values().forEach(ch->ch.deepPrint(ind+1));
    }

    public void deepPrint() {
        deepPrint(0);
    }

    public Parameters clone() {
        Parameters pc = new Parameters(NickName, PG, getLocus(), getLogPriorProb());
        if (isLikelihoodEvaluated()) pc.setLogLikelihood(getLogLikelihood());
        return pc;
    }


}
