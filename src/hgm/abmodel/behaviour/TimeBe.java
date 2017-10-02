package hgm.abmodel.behaviour;

import mcore.Clock;
import hgm.abmodel.Agent;
import hgm.abmodel.behaviour.trigger.Trigger;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class TimeBe extends AbsTimeDepBe {
    public TimeBe(String name, Clock clock) {
        super(name, Trigger.NullTrigger, clock);
    }

    @Override
    public void register(Agent ag, double ti) {

    }

}
