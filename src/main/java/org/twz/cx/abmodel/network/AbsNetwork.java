package org.twz.cx.abmodel.network;


import org.twz.cx.abmodel.Agent;
import org.twz.statistic.Statistics;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/8/12.
 */
public abstract class AbsNetwork extends HashMap<Agent, Set<Agent>> {
    private final String Name;

    protected AbsNetwork(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void addAgent(Agent ag) {
        put(ag, new HashSet<>());
        setConnection(ag);
    }

    public void removeAgent(Agent ag) {
        for (Set<Agent> nes: this.values()) {
            nes.remove(ag);
        }
        remove(ag);
    }

    public abstract void setConnection(Agent ag);

    protected void connect(Agent ag1, Agent ag2) {
        get(ag1).add(ag2);
        get(ag2).add(ag1);
    }

    public double meanDegree() {
        return Statistics.mean(values().stream().mapToDouble(Set::size).toArray());
    }

    public double varDegree() {
        return Statistics.var(values().stream().mapToDouble(Set::size).toArray());
    }

    public Set<Agent> getNeighbours(Agent ag) {
        return get(ag);
    }

    public abstract void reform();

}
