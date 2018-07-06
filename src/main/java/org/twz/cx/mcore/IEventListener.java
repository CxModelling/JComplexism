package org.twz.cx.mcore;

import org.twz.cx.element.Disclosure;

public interface IEventListener {
    <T> boolean needs(Disclosure d, AbsSimModel model_local);

    <T, E> void applyShock(Disclosure d, AbsSimModel model_foreign, AbsSimModel model_local, double ti);
}
