package org.twz.cx.abmodel.behaviour.trigger;


import org.twz.statespace.State;
import org.twz.statespace.Transition;
import org.twz.cx.abmodel.Agent;

/**
 *
 * Created by TimeWz on 2017/2/14.
 */
public class StateOutTrigger extends Trigger{
    private final State St;

    public StateOutTrigger(State st) {
        super();
        St = st;
    }

    public boolean checkTransition(Agent ag, Transition tr) {
        return (ag.getState().isa(St)) & (!ag.getState().exec(tr).isa(St));
    }

    public boolean checkOut(Agent ag) {
        return ag.getState().isa(St);
    }

}
