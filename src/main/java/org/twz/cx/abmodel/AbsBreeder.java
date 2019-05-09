package org.twz.cx.abmodel;

import org.json.JSONException;
import org.twz.dag.Chromosome;
import org.twz.dag.Parameters;
import org.twz.util.NameGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsBreeder<T extends AbsAgent> {
    private final String Group;
    private final NameGenerator GenName;
    private final Parameters GenPars;

    public AbsBreeder(String prefix, String group, Parameters genPars) {
        Group = group;
        GenName = new NameGenerator(prefix);
        GenPars = genPars.genPrototype(group);
    }

    public String getName() {
        return Group;
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
            pars = GenPars.genSibling(name);

            T ag = newAgent(name, pars, attributes);
            ag.updateAttributes(attributes);
            ags.add(ag);
            n --;
        }
        return ags;
    }

    protected abstract T newAgent(String name, Chromosome pars, Map<String, Object> attributes) throws JSONException;
}
