package org.twz.cx.abmodel.behaviour;

import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.element.Event;
import org.twz.cx.element.Ticker.AbsTicker;
import org.twz.cx.mcore.AbsSimModel;

public abstract class PassiveBehaviour extends AbsBehaviour {

    PassiveBehaviour(String name, Trigger tri) {
        super(name, tri);

    }

    PassiveBehaviour(String name, AbsTicker clock) {
        this(name, Trigger.NullTrigger);
    }

    @Override
    public final Event getNext() {
        return Event.NullEvent;
    }

    @Override
    public double getTTE() {
        return Double.POSITIVE_INFINITY;
    }
}
