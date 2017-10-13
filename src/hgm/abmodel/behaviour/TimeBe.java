package hgm.abmodel.behaviour;

import hgm.abmodel.Agent;
import hgm.abmodel.behaviour.trigger.Trigger;
import mcore.Ticker.ClockTicker;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class TimeBe extends AbsTimeDepBe {
    public TimeBe(String name, ClockTicker clock) {
        super(name, Trigger.NullTrigger, clock);
    }

    @Override
    public void register(Agent ag, double ti) {

    }

}
