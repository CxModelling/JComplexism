package org.twz.dag;

import java.util.Map;

public class PseudoParameters extends Parameters {
    public PseudoParameters(String nickname) {
        super(nickname, null, null, 0);
    }

    public PseudoParameters(String nickname, Map<String, Double> fixed) {
        super(nickname, null, fixed, 0);
    }

    @Override
    public String getGroupName() {
        return "Pseudo";
    }

    @Override
    public double getDouble(String s) {
        return Parent.getDouble(s);
    }

    @Override
    public Parameters breed(String nickname, String group, Map<String, Double> exo) {
        return this.breed(nickname, group);
    }

    @Override
    public Parameters breed(String nickname, String group) {
        Parameters pars = new PseudoParameters(nickname);
        pars.setParent(Parent);
        return Parent;
    }

    @Override
    public Parameters genSibling(String nickname, Map<String, Double> exo) {
        return Parent.genSibling(nickname, exo);
    }

    @Override
    public Parameters genSibling(String nickname) {
        return Parent.genSibling(nickname);
    }

    @Override
    public Parameters genPrototype(String group, Map<String, Double> exo) {
        return Parent.genPrototype(group, exo);
    }

    @Override
    public Parameters genPrototype(String group) {
        return Parent.genPrototype(group);
    }

    @Override
    public Parameters clone() {
        return Parent.clone();
    }
}
