package org.twz.dag;

import org.twz.dag.actor.SimulationActor;
import org.twz.prob.IDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public class ParameterCore extends Gene {

    private final String Nickname;
    private SimulationGroup SG;
    private ParameterCore Parent;
    private Map<String, SimulationActor> Actors;
    private Map<String, ParameterCore> Children;
    private Map<String, List<SimulationActor>> ChildrenActors;


    public ParameterCore(String nickname, SimulationGroup sg, Map<String, Double> fixed, double prior) {
        super(fixed, prior);
        Nickname = nickname;
        SG = sg;
        Children = new HashMap<>();
        Actors = new HashMap<>();
        ChildrenActors = new HashMap<>();
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

    public List<>

    public String toJSON() {
        String sb = "{";
        sb += getLocus().entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(","));
        sb += "}";
        return sb;
    }


    public ParameterCore clone() {
        return (ParameterCore) super.clone();
    }


}
