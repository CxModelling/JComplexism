package org.twz.dag;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.actor.CompoundActor;
import org.twz.dag.actor.Sampler;
import org.twz.dag.actor.SimulationActor;
import org.twz.exception.IncompleteConditionException;

import java.util.*;


public class Parameters extends Chromosome {
    public static Parameters NullParameters = new Parameters("Null", null, new HashMap<>(), 0);

    private String NickName;
    private ParameterGroup PG;
    Parameters Parent;
    Map<String, Sampler> Samplers;
    private Map<String, Parameters> Children;
    private boolean Frozen;

    public Parameters(String nickname, ParameterGroup sg, Map<String, Double> fixed, double prior) {
        super(fixed, prior);
        NickName = nickname;
        PG = sg;
        Children = new HashMap<>();
        Samplers = new HashMap<>();
        Frozen = false;
    }

    void setParent(Parameters parent) {
        Parent = parent;
    }

    public String getName() {
        return NickName;
    }

    public void rename(String name) {
        NickName = name;
    }

    public String getGroupName() {
        return PG.getName();
    }

    @Override
    public double getDouble(String s) {
        if (has(s)) {
            double v = super.getDouble(s);
            if (Double.isNaN(v)) {
                v = Parent.getDouble(s);
            }
            return v;
        } else {
            try {
                return Parent.getSampler(s).next();
            } catch (NullPointerException ex) {
                return getSampler(s).next();
            }
        }
    }

    @Override
    public boolean has(String s) {
        if (super.has(s)) {
            return true;
        } else {
            try {
                return Parent.has(s);
            } catch (NullPointerException ex) {
                return false;
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
        return Parent.breed(nickname, getGroupName(), null);
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
        for (String s : PG.getActorList()) {
            getSampler(s);
        }

        Parent.removeChild(NickName);
        if (collect) {
            collectLocus(getLocus());
            //for (String s : PG.getAvailableFixed()) {
            //    if (!super.has(s)) {
            //getLocus().put(s, Parent.getDouble(s));
            //    }
            //}
        }
        Parent = null;
    }

    private void collectLocus(Map<String, Double> end) {
        try {
            for (Map.Entry<String, Double> ent : Parent.getLocus().entrySet()) {
                end.putIfAbsent(ent.getKey(), ent.getValue());
            }
            Parent.collectLocus(end);
        } catch (NullPointerException ignored) {

        }
    }

    public void detachFromParent() {
        detachFromParent(true);
    }

    private void removeChild(String k) {
        Children.remove(k);
    }

    public List<String> listSamplers() {
        return PG.getActorList();
    }

    public Map<String, Sampler> getSamplers() {
        for (String s : PG.getActorList()) {
            getSampler(s);
        }
        return Samplers;
    }

    public Sampler getSampler(String sampler) {
        SimulationActor actor = PG.getActor(sampler, this);
        return new Sampler(actor, this);
    }

    public Parameters getChild(String chd) {
        return Children.get(chd);
    }

    public List<String> findAffectedActors(String imp) {
        return PG.getAffectedFloating(imp);
    }

    public void impulse(String k, double v) {
        Map<String, Double> imp = new HashMap<>();
        imp.put(k, v);
        impulse(imp);
    }

    public void impulse(Map<String, Double> imp) {
        PG.setResponse(imp, this);

        Children.values().forEach(ch->ch.impulse(imp));
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

        getSamplers();
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
