package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class InitialChecker extends AbsChecker {
    public InitialChecker() {
        super();
    }

    public InitialChecker(JSONObject js) {
        super(js);
    }

    @Override
    public AbsChecker deepcopy() {
        return new InitialChecker();
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.What.startsWith("initialise");
    }
}
