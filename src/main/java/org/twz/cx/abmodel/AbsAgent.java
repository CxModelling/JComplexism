package org.twz.cx.abmodel;

import org.twz.cx.element.ModelAtom;
import org.twz.dag.Chromosome;

public abstract class AbsAgent extends ModelAtom {
    public AbsAgent(String name, Chromosome pars) {
        super(name, pars);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + getName() + ", " +
                "Parameters=" + Parameters.getLocus() +
                "Attributes=" + Attributes +
                '}';
    }
}
