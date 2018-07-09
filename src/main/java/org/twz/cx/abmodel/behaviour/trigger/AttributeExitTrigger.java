package org.twz.cx.abmodel.behaviour.trigger;

import org.twz.cx.abmodel.AbsAgent;

import java.util.Map;

public class AttributeExitTrigger extends AttributeTrigger {
    public AttributeExitTrigger(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public boolean checkChange(boolean pre, boolean post) {
        return pre & (!post);
    }

    @Override
    public boolean checkExit(AbsAgent ag) {
        return super.checkExit(ag);
    }
}
