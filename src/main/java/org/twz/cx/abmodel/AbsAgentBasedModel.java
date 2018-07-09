package org.twz.cx.abmodel;

import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.abmodel.network.AbsNetwork;
import org.twz.cx.mcore.AbsObserver;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.LeafModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TimeWz on 09/07/2018.
 */
public abstract class AbsAgentBasedModel<T extends AbsAgent> extends LeafModel {

    private Population<T> Population;
    private Map<String, AbsBehaviour> Behaviours;

    public AbsAgentBasedModel(String name, Map<String, Object> env, Population<T> pop, AbsObserver<AbsSimModel> obs, IY0 protoY0) {
        super(name, env, obs, protoY0);
        this.Population = pop;
        Behaviours = new LinkedHashMap<>();
    }

    public void addNetwork(AbsNetwork net) {
        this.Population.addNetwork(net);
    }

    protected void makeAgents(int n, double ti, Map<String, Object> attributes) {
        List<T> ags = this.Population.addAgents(n, attributes);
        for (T ag: ags) {
            for (AbsBehaviour be: Behaviours.values()) {
                be.register(ag, ti);
            }
        }
    }

    @Override
    public void preset(double ti) {
        Behaviours.values().forEach(be->be.initialise(ti, this));
        this.Population.getAgents().values().forEach(ag -> ag.initialise(ti, this));
        disclose("initialise", "*");
    }


    @Override
    public void reset(double ti) {
        Behaviours.values().forEach(be->be.reset(ti, this));
        this.Population.getAgents().values().forEach(ag -> ag.reset(ti, this));
        disclose("initialise", "*");
    }
}
