package org.twz.cx.abmodel.behaviour;

import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.element.Event;
import org.twz.cx.element.Ticker.AbsTicker;
import org.twz.cx.mcore.AbsSimModel;

public abstract class PassiveBehaviour extends AbsBehaviour {

    public PassiveBehaviour(String name, Trigger tri) {
        super(name, tri);

    }

    public PassiveBehaviour(String name) {
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

    @Override
    protected Event findNext() {
        return Event.NullEvent;
    }

    @Override
    public void executeEvent() {

    }

    @Override
    public void updateTo(double ti) {

    }
}
