package org.twz.cx.abmodel.behaviour;

import org.twz.cx.abmodel.Agent;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.abmodel.modifier.AbsModifier;
import org.twz.cx.element.Ticker.StepTicker;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class TimeModBe extends AbsTimeDepBe {
    protected final AbsModifier ModPrototype;

    public TimeModBe(String name, Trigger tri, StepTicker clock, AbsModifier mod) {
        super(name, tri, clock);
        ModPrototype = mod;
    }

    @Override
    public void register(Agent ag, double ti) {
        ag.addMod(ModPrototype.clone());
    }


}
