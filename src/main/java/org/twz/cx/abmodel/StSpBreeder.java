package org.twz.cx.abmodel;


import org.twz.dag.ParameterGenerator;
import org.twz.statespace.AbsDCore;
import org.twz.statespace.State;

import java.util.Collection;
import java.util.Map;

public class StSpBreeder extends AbsBreeder<StSpAgent> {
    private final AbsDCore DCore;
    private Map<String, State> WellDefined;

    public StSpBreeder(String name, String group, AbsDCore dc, ParameterGenerator genPars, Map<String, Object> exo) {
        super(name, group, genPars, exo);
        DCore = dc;
        WellDefined = dc.getWellDefinedStateSpace();
    }

    public AbsDCore getDCore() {
        return DCore;
    }

    @Override
    protected StSpAgent newAgent(String name, Map<String, Object> pars, Map<String, Object> attributes) {
        State st = WellDefined.get(attributes.get("st"));
        StSpAgent ag = new StSpAgent(name, pars, st);
        attributes.entrySet().stream().filter(a -> !"st".equals(a.getKey())).forEach(a->ag.put(a.getKey(), a.getValue()));
        return ag;
    }

    public long count(Collection<StSpAgent> ags, Object st) {
        State state;
        if (st instanceof State) {
            state = (State) st;
        } else {
            state = DCore.getState((String) st);
        }
        return ags.stream().filter(ag -> ag.getState() == state).count();
    }
}
