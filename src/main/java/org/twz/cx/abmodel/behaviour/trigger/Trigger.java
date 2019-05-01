package org.twz.cx.abmodel.behaviour.trigger;


import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.element.Event;
import org.twz.statespace.Transition;
import org.twz.cx.mcore.AbsSimModel;

/**
 *
 * Created by TimeWz on 2017/2/14.
 */
public class Trigger {
    public final static Trigger NullTrigger = new Trigger();

    public boolean checkPreChange(AbsAgent ag) {
        return false;
    }

    public boolean checkPostChange(AbsAgent ag) {
        return false;
    }

    public boolean checkChange(boolean pre, boolean post) {
        return false;
    }

    public boolean checkEnter(AbsAgent ag) {
        return false;
    }

    public boolean checkExit(AbsAgent ag) {
        return false;
    }

}
