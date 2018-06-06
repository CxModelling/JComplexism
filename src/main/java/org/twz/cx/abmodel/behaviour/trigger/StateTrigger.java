package org.twz.cx.abmodel.behaviour.trigger;


import org.twz.statespace.State;
import org.twz.statespace.Transition;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.abmodel.Agent;

/**
 *
 * Created by TimeWz on 2017/2/14.
 */
public class StateTrigger extends Trigger{
    private final State St;

    public StateTrigger(State st) {
        super();
        St = st;
    }

    public boolean checkTransition(Agent ag, Transition tr) {
        return ag.getState().isa(St) ^ (ag.getState().exec(tr).isa(St));
    }

    public boolean checkIn(Agent ag) {
        return ag.getState().isa(St);
    }

    public boolean checkOut(Agent ag) {
        return ag.getState().isa(St);
    }

    public boolean checkForeign(AbsSimModel model, String node) {
        return false;
    }
}
