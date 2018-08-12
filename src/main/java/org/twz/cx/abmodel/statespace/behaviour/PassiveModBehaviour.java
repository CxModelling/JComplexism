package org.twz.cx.abmodel.statespace.behaviour;

import org.twz.cx.abmodel.AbsAgent;
import org.twz.cx.abmodel.behaviour.ActiveBehaviour;
import org.twz.cx.abmodel.behaviour.PassiveBehaviour;
import org.twz.cx.abmodel.behaviour.trigger.Trigger;
import org.twz.cx.abmodel.statespace.StSpAgent;
import org.twz.cx.abmodel.statespace.modifier.AbsModifier;
import org.twz.cx.element.Ticker.AbsTicker;

public abstract class PassiveModBehaviour extends PassiveBehaviour {
    protected AbsModifier ModProto;

    PassiveModBehaviour(String name, AbsModifier mod, Trigger tri) {
        super(name, tri);
        ModProto = mod;
    }

    @Override
    public void register(AbsAgent ag, double ti) {
        ((StSpAgent) ag).addMod(ModProto.clone());
    }
}
