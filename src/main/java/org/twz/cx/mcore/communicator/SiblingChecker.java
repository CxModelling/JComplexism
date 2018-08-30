package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.element.Disclosure;

public class SiblingChecker extends AbsChecker {

    public SiblingChecker() {
        super();
    }

    public SiblingChecker(JSONObject js) {
        super(js);
    }

    @Override
    public boolean check(Disclosure dis) {
        return dis.isSibling();
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        return super.toJSON();
    }

    public AbsChecker deepcopy() {
        return new SiblingChecker();
    }
}
