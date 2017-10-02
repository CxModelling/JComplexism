package hgm.abmodel.behaviour;

import mcore.AbsSimModel;
import mcore.Event;
import mcore.Clock;
import hgm.abmodel.Agent;
import hgm.abmodel.AgentBasedModel;
import hgm.abmodel.behaviour.trigger.Trigger;

/**
 *
 * Created by TimeWz on 2017/2/15.
 */
public abstract class AbsTimeDepBe extends AbsBehaviour {

    protected Clock Ticker;
    protected Event Next;

    AbsTimeDepBe(String name, Trigger tri, Clock clock) {
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
