package hgm.abmodel.behaviour;

import mcore.Clock;
import hgm.abmodel.Agent;
import hgm.abmodel.behaviour.trigger.Trigger;
import hgm.abmodel.modifier.AbsModifier;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class TimeModBe extends AbsTimeDepBe {
    protected final AbsModifier ModPrototype;

    public TimeModBe(String name, Trigger tri, Clock clock, AbsModifier mod) {
        super(name, tri, clock);
        ModPrototype = mod;
    }

    @Override
    public void register(Agent ag, double ti) {
        ag.addMod(ModPrototype.clone());
    }


}
