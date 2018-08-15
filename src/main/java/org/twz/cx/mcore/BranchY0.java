package org.twz.cx.mcore;

import java.util.*;

public abstract class BranchY0 implements IY0 {
    private Map<String, IY0> Children;

    public BranchY0() {
        Children = new HashMap<>();
    }

    public IY0 getChildren(String key) {
        return Children.get(key);
    }

    public void appendChildren(String key, IY0 chd) {
        Children.put(key, chd);
    }
}
