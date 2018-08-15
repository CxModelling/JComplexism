package org.twz.cx.abmodel.statespace;


import org.twz.cx.abmodel.AbsBreeder;
import org.twz.dag.Gene;
import org.twz.dag.ParameterCore;
import org.twz.statespace.AbsStateSpace;
import org.twz.statespace.State;

import java.util.Collection;
import java.util.Map;

public class StSpBreeder extends AbsBreeder<StSpAgent> {
    private final AbsStateSpace DCore;
    private Map<String, State> WellDefined;

    public StSpBreeder(String name, String group, AbsStateSpace dc, ParameterCore genPars, Map<String, Double> exo) {
        super(name, group, genPars, exo);
        DCore = dc;
        WellDefined = dc.getWellDefinedStateSpace();
    }

    public AbsStateSpace getDCore() {
        return DCore;
    }

    protected StSpAgent newAgent(String name, Gene pars, Map<String, Object> attributes) {
        Object st_def = attributes.get("st");
        State st;
        if (st_def instanceof State) {
            st = (State) st_def;
        } else {
            st = WellDefined.get(st_def);
        }

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
        return ags.stream().filter(ag -> ag.getState().isa(state)).count();
    }

}
