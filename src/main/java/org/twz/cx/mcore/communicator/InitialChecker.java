package org.twz.cx.mcore.communicator;

import org.twz.cx.element.Disclosure;

public class InitialChecker extends AbsChecker {

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.startsWith("initialise");
    }

}
