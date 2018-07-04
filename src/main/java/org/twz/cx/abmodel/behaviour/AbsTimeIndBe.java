package org.twz.cx.abmodel.behaviour;

import org.twz.cx.element.Event;
import org.twz.cx.abmodel.AgentBasedModel;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class AbsTimeIndBe extends AbsBehaviour {

    AbsTimeIndBe(String name, Trigger tri) {
        super(name, tri);
    }

    @Override
    public Event next() {
        return Event.NullEvent;
    }

    @Override
    public double tte() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public void dropNext() {

    }

    @Override
    public void assign(Event evt) {

    }

    @Override
    public void exec(AgentBasedModel model, Event evt) {

    }
}
