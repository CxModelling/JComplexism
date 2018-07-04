package org.twz.cx.abmodel.behaviour;

import org.twz.cx.abmodel.Agent;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.element.Ticker.StepTicker;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class TimeBe extends AbsTimeDepBe {
    public TimeBe(String name, StepTicker clock) {
        super(name, Trigger.NullTrigger, clock);
    }

    @Override
    public void register(Agent ag, double ti) {

    }

}
