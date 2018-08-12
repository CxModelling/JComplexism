package org.twz.cx.abmodel.statespace;

import org.twz.cx.abmodel.AbsBreeder;
import org.twz.cx.abmodel.Population;
import org.twz.dag.ParameterCore;
import org.twz.statespace.AbsDCore;

import java.util.HashMap;
import java.util.Map;

public class StSpPopulation extends Population<StSpAgent> {
    public StSpPopulation(AbsBreeder<StSpAgent> eva) {
        super(eva);
    }

    public StSpPopulation(String name, String group, AbsDCore dc, ParameterCore genPars, Map<String, Double> exo) {
        super(new StSpBreeder(name, group, dc, genPars, exo));
    }

    public StSpPopulation(String name, String group, AbsDCore dc, ParameterCore genPars) {
        super(new StSpBreeder(name, group, dc, genPars, new HashMap<>()));
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
