package org.twz.cx.abmodel.behaviour;

import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.Event;
import org.twz.cx.abmodel.Agent;
import org.twz.cx.abmodel.AgentBasedModel;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.mcore.Ticker.ClockTicker;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class AbsTimeDepBe extends AbsBehaviour {

    protected ClockTicker Ticker;
    protected Event Next;

    AbsTimeDepBe(String name, Trigger tri, ClockTicker clock) {
        super(name, tri);
        Ticker = clock;
        Next = Event.NullEvent;
    }

    @Override
    public Event next() {
        if (Next == Event.NullEvent) {
            findNext();
        }
        return Next;
    }

    private void findNext() {
        double ti = Ticker.getNext();
        Next = composeEvent(ti);
    }

    protected abstract Event composeEvent(double ti);

    @Override
    public double tte() {
        return Next.getTime();
    }

    @Override
    public void dropNext() {
        Next = Event.NullEvent;
    }

    @Override
    public void assign(Event evt) {
        Next = evt;
    }

    @Override
    public void exec(AgentBasedModel model, Event evt) {
        Ticker.update(evt.getTime());
        doRequest(model, evt, evt.getTime());
        dropNext();
    }

    protected abstract void doRequest(AgentBasedModel model, Event evt, double ti);

    @Override
    public void impulseTransition(AgentBasedModel model, Agent ag, double ti) {

    }

    @Override
    public void impulseIn(AgentBasedModel model, Agent ag, double ti) {

    }

    @Override
    public void impulseOut(AgentBasedModel model, Agent ag, double ti) {

    }

    @Override
    public void impulseForeign(AbsSimModel fore, String node) {

    }
}
