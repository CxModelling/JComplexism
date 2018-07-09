package org.twz.cx.abmodel;

import org.twz.cx.mcore.ModelAtom;

import java.util.Map;

public abstract class AbsAgent extends ModelAtom {
    public AbsAgent(String name, Map<String, Object> pars) {
        super(name, pars);
    }

    @Override
    public String toString() {
        return "AbsAgent{" + getName() + ", " +
                "Attributes=" + Attributes +
                '}';
    }
}
