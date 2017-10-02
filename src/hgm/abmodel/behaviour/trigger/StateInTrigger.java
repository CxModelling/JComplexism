package hgm.abmodel.behaviour.trigger;


import dcore.State;
import dcore.Transition;
import hgm.abmodel.Agent;

/**
 *
 * Created by TimeWz on 2017/2/14.
 */
public class StateInTrigger extends Trigger{
    private final State St;

    public StateInTrigger(State st) {
        super();
        St = st;
    }

    public boolean checkTransition(Agent ag, Transition tr) {
        return (!ag.getState().isa(St)) & (ag.getState().exec(tr).isa(St));
    }

    public boolean checkIn(Agent ag) {
        return ag.getState().isa(St);
    }

}
