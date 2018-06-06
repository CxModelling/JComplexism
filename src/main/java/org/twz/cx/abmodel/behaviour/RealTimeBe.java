package org.twz.cx.abmodel.behaviour;

import org.twz.cx.abmodel.Agent;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class RealTimeBe extends AbsTimeIndBe {

    public RealTimeBe(String name, Trigger tri) {
        super(name, tri);
    }

    @Override
    public void register(Agent ag, double ti) {

    }
}
