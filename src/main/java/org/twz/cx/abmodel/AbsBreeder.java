package org.twz.cx.abmodel;

import org.twz.dag.Gene;
import org.twz.dag.ParameterCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsBreeder<T extends AbsAgent> {
    private final String Name, Group;
    private final NameGenerator GenName;
    private final Gene GenPars;
    private final Map<String, Double> Exo;

    public AbsBreeder(String name, String group, Gene genPars, Map<String, Double> exo) {
        Name = name;
        Group = group;
        GenName = new NameGenerator(name);
        GenPars = genPars;
        Exo = exo;
    }

    public String getName() {
        return Name;
    }

    public List<T> breed(int n, Map<String, Object> attributes) {
        String name;
        Gene pars;
        List<T> ags = new ArrayList<>();
        while (n > 0) {
            name = GenName.getNext();
            if (GenPars instanceof ParameterCore) {
                pars = ((ParameterCore) GenPars).breed(name, Group, Exo);
            } else {
                pars = GenPars.clone();
            }
            T ag = newAgent(name, pars, attributes);
            ag.updateAttributes(attributes);
            ags.add(ag);
            n --;
        }
        return ags;
    }

    protected abstract T newAgent(String name, Gene pars, Map<String, Object> attributes);
}
