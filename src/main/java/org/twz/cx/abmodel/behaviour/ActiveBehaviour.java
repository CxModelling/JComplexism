package org.twz.cx.abmodel.behaviour;

import org.json.JSONException;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.element.Event;
import org.twz.cx.element.Ticker.AbsTicker;
import org.twz.cx.mcore.AbsSimModel;

public abstract class ActiveBehaviour extends AbsBehaviour {
    private AbsTicker Clock;


    public ActiveBehaviour(String name, AbsTicker clock, Trigger tri) {
        super(name, tri);
        Clock = clock;
    }

    ActiveBehaviour(String name, AbsTicker clock) {
        this(name, clock, Trigger.NullTrigger);
    }

    @Override
    protected Event findNext() {
        return composeEvent(Clock.getNext());
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        Clock.initialise(ti);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        Clock.initialise(ti);
    }

    @Override
    public void updateTo(double ti) {
        Clock.update(ti);
    }

    protected abstract Event composeEvent(double ti);

    @Override
    public final void executeEvent() {

    }

    public void operate(AbsSimModel model) throws JSONException {
        Event evt = getNext();
        double time = evt.getTime();
        Clock.update(time);
        doAction(model, evt.getValue(), time);
        dropNext();
    }

    protected abstract void doAction(AbsSimModel model, Object todo, double ti) throws JSONException;
}
