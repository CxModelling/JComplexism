package org.twz.cx.abmodel.statespace;

import org.twz.cx.abmodel.AbsBreeder;
import org.twz.cx.abmodel.Population;
import org.twz.dag.Parameters;
import org.twz.statespace.AbsStateSpace;

import java.util.HashMap;
import java.util.Map;

public class StSpPopulation extends Population<StSpAgent> {
    public StSpPopulation(AbsBreeder<StSpAgent> eva) {
        super(eva);
    }

    public StSpPopulation(String name, String group, AbsStateSpace dc, Parameters genPars) {
        super(new StSpBreeder(name, group, dc, genPars));
    }

    @Override
    public long count(String key, Object value) {
        if (key.equals("stat")) {
            return ((StSpBreeder) getEva()).count(getAgents().values(), value);
        } else {
            return super.count(key, value);
        }
    }
}
