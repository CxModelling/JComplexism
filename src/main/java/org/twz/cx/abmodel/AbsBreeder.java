package org.twz.cx.abmodel;

import org.json.JSONException;
import org.twz.dag.Chromosome;
import org.twz.dag.Parameters;
import org.twz.util.NameGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsBreeder<T extends AbsAgent> {
    private final String Prefix, Group;
    private final NameGenerator GenName;
    private final Parameters GenPars;
    private final Map<String, Double> Exo;

    public AbsBreeder(String prefix, String group, Parameters genPars, Map<String, Double> exo) {
        Prefix = prefix;
        Group = group;
        GenName = new NameGenerator(prefix);
        GenPars = genPars.genPrototype(group);
        Exo = exo;
    }

    public String getName() {
        return Group;
    }

    public String getPrefix() {
        return Prefix;
    }

    public Parameters getPrototype() {
        return GenPars;
    }

    public List<T> breed(int n, Map<String, Object> attributes) throws JSONException {
        String name;
        Chromosome pars;
        List<T> ags = new ArrayList<>();
        while (n > 0) {
            name = GenName.getNext();
            pars = GenPars.genSibling(name, Exo);

            T ag = newAgent(name, pars, attributes);
            ag.updateAttributes(attributes);
            ags.add(ag);
            n --;
        }
        return ags;
    }

    protected abstract T newAgent(String name, Chromosome pars, Map<String, Object> attributes) throws JSONException;
}
