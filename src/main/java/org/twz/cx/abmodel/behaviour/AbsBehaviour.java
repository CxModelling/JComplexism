package org.twz.cx.abmodel.behaviour;

import org.twz.cx.element.Event;
import org.twz.statespace.Transition;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.abmodel.Agent;
import org.twz.cx.abmodel.AgentBasedModel;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsBehaviour {
    private String Name;
    private Trigger Tri;

    AbsBehaviour(String name, Trigger tri) {
        Name = name;
        Tri = tri;
    }

    public String getName() {
        return Name;
    }

    public abstract void initialise(AgentBasedModel model, double ti);

    public abstract void register(Agent ag, double ti);

    public boolean checkTransition(Agent ag, Transition tr) {
        return Tri.checkTransition(ag, tr);
    }

    public abstract void impulseTransition(AgentBasedModel model, Agent ag, double ti);

    public boolean checkIn(Agent ag) {
        return Tri.checkIn(ag);
    }

    public abstract void impulseIn(AgentBasedModel model, Agent ag, double ti);

    public boolean checkOut(Agent ag) {
        return Tri.checkOut(ag);
    }

    public abstract void impulseOut(AgentBasedModel model, Agent ag, double ti);

    public boolean chechForeign(AbsSimModel fore, String node) {
        return Tri.checkForeign(fore, node);
    }

    public abstract void impulseForeign(AbsSimModel fore, String node);

    public abstract void fill(Map<String, Double> Last, AgentBasedModel model, double ti);


    public abstract Event next();

    public abstract double tte();

    public abstract void dropNext();

    public abstract void assign(Event evt);

    public abstract void exec(AgentBasedModel model, Event evt);

}
