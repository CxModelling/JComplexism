package org.twz.cx.mcore.communicator;

import org.twz.cx.element.Disclosure;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.io.AdapterJSONObject;

public interface IShocker extends AdapterJSONObject {
    void shock(Disclosure dis, AbsSimModel source, AbsSimModel target, double time);
}
