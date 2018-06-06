package org.twz.cx.abmodel.behaviour;

import org.twz.cx.abmodel.Agent;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.abmodel.modifier.AbsModifier;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class ModBe extends AbsTimeIndBe {
    protected final AbsModifier ModPrototype;

    public ModBe(String name, Trigger tri, AbsModifier mod) {
        super(name, tri);
        ModPrototype = mod;
    }

    @Override
    public void register(Agent ag, double ti) {
        ag.addMod(ModPrototype.clone());
    }
}
