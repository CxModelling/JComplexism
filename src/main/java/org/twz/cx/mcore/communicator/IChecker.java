package org.twz.cx.mcore.communicator;

import org.twz.cx.element.Disclosure;
import org.twz.io.AdapterJSONObject;

public interface IChecker extends AdapterJSONObject {
    boolean check(Disclosure dis);
}
