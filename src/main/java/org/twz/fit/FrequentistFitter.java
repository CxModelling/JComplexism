package org.twz.fit;

public abstract class FrequentistFitter extends AbsFitter {
    public FrequentistFitter() {
        super();
        Options.put("Type", "MLE");
    }
}
