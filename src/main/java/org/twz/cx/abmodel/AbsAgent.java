package org.twz.cx.abmodel;

import org.twz.cx.element.ModelAtom;
import org.twz.dag.Chromosome;

import java.util.Map;

public abstract class AbsAgent extends ModelAtom {
    public AbsAgent(String name, Chromosome pars) {
        super(name, pars);
    }

    public boolean isa(Map<String, Object> kvs) {
        for (Map.Entry<String, Object> ent : kvs.entrySet()) {
            if (!isa(ent.getKey(), ent.getValue())) {
                return false;
            }
        }
        return true;
    }

    public boolean isa(String k, Object v) {
        try {
            return get(k).equals(v);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + getName() +
                // ", Parameters=" + Parameters.getLocus() +
                ", Attributes=" + Attributes +
                '}';
    }
}
