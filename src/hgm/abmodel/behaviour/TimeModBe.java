package hgm.abmodel.behaviour;

import hgm.abmodel.Agent;
import hgm.abmodel.behaviour.trigger.Trigger;
import hgm.abmodel.modifier.AbsModifier;
import mcore.Ticker.ClockTicker;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class TimeModBe extends AbsTimeDepBe {
    protected final AbsModifier ModPrototype;

    public TimeModBe(String name, Trigger tri, ClockTicker clock, AbsModifier mod) {
        super(name, tri, clock);
        ModPrototype = mod;
    }

    @Override
    public void register(Agent ag, double ti) {
        ag.addMod(ModPrototype.clone());
    }


}
