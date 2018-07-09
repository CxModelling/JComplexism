package org.twz.cx.abmodel.behaviour.trigger;

import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.element.Event;

public class EventTrigger extends Trigger {
    private final Object Todo;

    public EventTrigger(Object todo) {
        Todo = todo;
    }

    @Override
    public boolean checkEvent(AbsAgent ag, Event evt) {
        return evt.getValue() == Todo;
    }
}
