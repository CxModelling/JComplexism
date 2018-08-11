package org.twz.cx.abmodel.statespace;

import org.twz.cx.abmodel.AbsBreeder;
import org.twz.cx.abmodel.Population;

public class StSpPopulation extends Population<StSpAgent> {
    public StSpPopulation(AbsBreeder<StSpAgent> eva) {
        super(eva);
    }

    @Override
    public long count(String key, Object value) {
        if (key.equals("st")) {
            return ((StSpBreeder) getEva()).count(getAgents().values(), value);
        } else {
            return super.count(key, value);
        }
    }
}
