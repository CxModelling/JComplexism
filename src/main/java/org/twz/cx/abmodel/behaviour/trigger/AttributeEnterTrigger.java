package org.twz.cx.abmodel.behaviour.trigger;

import org.twz.cx.abmodel.AbsAgent;

import java.util.Map;

public class AttributeEnterTrigger extends AttributeTrigger {

    public AttributeEnterTrigger(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public boolean checkChange(boolean pre, boolean post) {
        return !pre & post;
    }

    @Override
    public boolean checkEnter(AbsAgent ag) {
        return super.checkEnter(ag);
    }
}
