package org.twz.cx.abmodel.statespace.behaviour.trigger;

import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.abmodel.statespace.StSpAgent;
import org.twz.statespace.State;

public class StateExitTrigger extends Trigger {
    private final State St;

    public StateExitTrigger(State st) {
        St = st;
    }

    private boolean check(AbsAgent agent) {
        return ((StSpAgent) agent).isa(St);
    }

    @Override
    public boolean checkPostChange(AbsAgent ag) {
        return check(ag);
    }

    @Override
    public boolean checkPreChange(AbsAgent ag) {
        return check(ag);
    }

    @Override
    public boolean checkChange(boolean pre, boolean post) {
        return pre && (!post);
    }

    @Override
    public boolean checkExit(AbsAgent ag) {
        return check(ag);
    }

}
