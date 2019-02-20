package org.twz.cx.abmodel.network;


import org.twz.cx.abmodel.AbsAgent;
import org.twz.util.Statistics;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/8/12.
 */
public abstract class AbsNetwork extends HashMap<AbsAgent, Set<AbsAgent>> {
    private final String Name;

    protected AbsNetwork(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void addAgent(AbsAgent ag) {
        put(ag, new HashSet<>());
        setConnection(ag);
    }

    public void removeAgent(AbsAgent ag) {
        for (Set<AbsAgent> nes: this.values()) {
            nes.remove(ag);
        }
        remove(ag);
    }

    public abstract void setConnection(AbsAgent ag);

    protected void connect(AbsAgent ag1, AbsAgent ag2) {
        get(ag1).add(ag2);
        get(ag2).add(ag1);
    }

    public double meanDegree() {
        return Statistics.mean(values().stream().mapToDouble(Set::size).toArray());
    }

    public double varDegree() {
        return Statistics.var(values().stream().mapToDouble(Set::size).toArray());
    }

    public Set<AbsAgent> getNeighbours(AbsAgent ag) {
        return get(ag);
    }

    public abstract void reform();

}
