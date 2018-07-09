package org.twz.cx.abmodel.behaviour.trigger;

import org.twz.cx.abmodel.AbsAgent;

import java.util.Map;

public class AttributeTrigger extends Trigger {
    private final Map<String, Object> Attributes;

    public AttributeTrigger(Map<String, Object> attributes) {
        Attributes = attributes;
    }

    protected boolean check(AbsAgent ag) {
        for (Map.Entry<String, Object> ent: Attributes.entrySet()) {
            if (ag.get(ent.getKey()) != ent.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkPreChange(AbsAgent ag) {
        return super.checkPreChange(ag);
    }

    @Override
    public boolean checkPostChange(AbsAgent ag) {
        return super.checkPostChange(ag);
    }

    @Override
    public boolean checkChange(boolean pre, boolean post) {
        return pre ^ post;
    }
}
