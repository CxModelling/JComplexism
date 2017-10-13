package hgm.abmodel.behaviour.trigger;


import dcore.Transition;
import mcore.AbsSimModel;
import hgm.abmodel.Agent;

/**
 *
 * Created by TimeWz on 2017/2/14.
 */
public class Trigger {
    public final static Trigger NullTrigger = new Trigger();


    public boolean checkTransition(Agent ag, Transition tr) {
        return false;
    }

    public boolean checkIn(Agent ag) {
        return false;
    }

    public boolean checkOut(Agent ag) {
        return false;
    }

    public boolean checkForeign(AbsSimModel model, String node) {
        return false;
    }
}
