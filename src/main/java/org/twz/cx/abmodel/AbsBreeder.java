package org.twz.cx.abmodel;

import org.twz.dag.ParameterGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsBreeder<T extends AbsAgent> {
    private final String Name, Group;
    private final NameGenerator GenName;
    private final ParameterGenerator GenPars;
    private final Map<String, Object> Exo;

    public AbsBreeder(String name, String group, ParameterGenerator genPars, Map<String, Object> exo) {
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
        Map<String, Object> pars;
        List<T> ags = new ArrayList<>();
        while (n > 0) {
            name = GenName.getNext();
            pars = GenPars.getParameters(Group, Exo);
            ags.add(newAgent(name, pars, attributes));
            n --;
        }
        return ags;
    }

    protected abstract T newAgent(String name, Map<String, Object> pars, Map<String, Object> attributes);
}
