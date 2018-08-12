package org.twz.cx.abmodel.behaviour;

import org.json.JSONObject;
import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.element.Event;
import org.twz.cx.element.ModelAtom;
import org.twz.cx.mcore.*;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsBehaviour extends ModelAtom {
    private Trigger Tri;

    AbsBehaviour(String name, Trigger tri) {
        super(name);
        Tri = tri;
    }

    public abstract void register(AbsAgent ag, double ti);

    public boolean checkEvent(AbsAgent ag, Event evt) {
        return Tri.checkEvent(ag, evt);
    }

    public boolean checkPreChange(AbsAgent ag) {
        return Tri.checkPreChange(ag);
    }

    public boolean checkPostChange(AbsAgent ag) {
        return Tri.checkPostChange(ag);
    }

    public boolean checkChange(boolean pre, boolean post) {
        return Tri.checkChange(pre, post);
    }

    public abstract void impulseChange(AbsSimModel model, AbsAgent ag, double ti);

    public boolean checkEnterChange(AbsAgent ag) {
        return Tri.checkEnter(ag);
    }

    public abstract void impulseEnter(AbsSimModel model, AbsAgent ag, double ti);

    public boolean checkExitChange(AbsAgent ag) {
        return Tri.checkExit(ag);
    }

    public abstract void impulseExit(AbsSimModel model, AbsAgent ag, double ti);

    public abstract void fillData(Map<String, Double> obs, AbsSimModel model, double ti);

    public abstract void match(AbsBehaviour be_src, Map<String, AbsAgent> ags_src, Map<String, AbsAgent> ags_new, double ti);

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Name", getName());
        js.put("Type", getClass().getSimpleName());
        js.put("Args", getArgumentJSON());
        return js;
    }

    protected abstract JSONObject getArgumentJSON();
}
