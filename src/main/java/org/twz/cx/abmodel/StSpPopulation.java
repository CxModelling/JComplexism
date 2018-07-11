package org.twz.cx.abmodel;

import java.util.List;
import java.util.Map;

public class StSpPopulation extends Population<StSpAgent> {
    public StSpPopulation(AbsBreeder<StSpAgent> eva) {
        super(eva);
    }

    @Override
    public long count(String key, Object value) {
        if (key.equals("st")) {
            return ((StSpBreeder) getEva()).count(getAgents().values(), (String) value);
        } else {
            return super.count(key, value);
        }
    }
}
