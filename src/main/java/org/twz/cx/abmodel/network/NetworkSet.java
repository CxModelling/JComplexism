package org.twz.cx.abmodel.network;

import org.twz.cx.abmodel.Agent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */

public class NetworkSet extends HashMap<String, AbsNetwork> {
    private Map<String, AbsNetwork> Networks;

    public NetworkSet() {
        Networks = new HashMap<>();
    }

    public void append(AbsNetwork net) {
        Networks.put(net.getName(), net);
    }

    public void reform(String net) {
        try{
            this.get(net).reform();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void reform() {
        this.values().forEach(AbsNetwork::reform);
    }

    public void addAgent(Agent ag) {
        this.values().forEach(e->e.addAgent(ag));
    }

    public void removeAgent(Agent ag) {
        this.values().forEach(e->e.removeAgent(ag));
    }

    public Set<Agent> getNeighbours(Agent ag, String net) {
        try {
            return new HashSet<>(get(net).getNeighbours(ag));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Set<Agent>> getNeighbours(Agent ag) {
        HashMap<String, Set<Agent>> nes = new HashMap<>();
        for (Entry<String, AbsNetwork> ent: entrySet()) {
           nes.put(ent.getKey(), getNeighbours(ag, ent.getKey()));
        }
        return nes;
    }

    public Set<Agent> getNeighbourSet(Agent ag) {
        HashSet<Agent> nes = new HashSet<>();
        for (Entry<String, AbsNetwork> ent: entrySet()) {
            nes.addAll(getNeighbours(ag, ent.getKey()));
        }
        return nes;
    }

    public void clear(String net) {
        try {
            get(net).clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        values().forEach(HashMap::clear);
    }

    public void match(NetworkSet source) {

    }

}
