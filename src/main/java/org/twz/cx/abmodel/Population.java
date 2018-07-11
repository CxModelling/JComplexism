package org.twz.cx.abmodel;

import org.twz.cx.abmodel.network.AbsNetwork;
import org.twz.cx.abmodel.network.NetworkSet;
import org.twz.io.AdapterJSONObject;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.*;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class Population<T extends AbsAgent> implements AdapterJSONObject {
    private AbsBreeder<T> Eva;
    private Map<String, T> Agents;
    private NetworkSet Networks;


    public Population(AbsBreeder<T> eva) {
        Eva = eva;
        Agents = new HashMap<>();
        Networks = new NetworkSet();
    }

    public AbsBreeder<T> getEva() {
        return Eva;
    }

    public void addNetwork(AbsNetwork net) {
        Networks.append(net);
    }

    public T get(String name) {
        return Agents.get(name);
    }

    public Map<String, T> getAgents() {
        return Agents;
    }

    public List<T> addAgents(int n, Map<String, Object> info) {
        List<T> ags = Eva.breed(n, info);
        for (T ag: ags) {
            Agents.put(ag.getName(), ag);
            Networks.addAgent(ag);
        }
        return ags;
    }

    public List<T> addAgents(int n) {
        return addAgents(n, new LinkedHashMap<>());
    }

    public AbsAgent removeAgent(String id) {
        AbsAgent ag;
        try {
            ag = Agents.remove(id);
            Networks.removeAgent(ag);
            return ag;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void reformNetworks(String net) {
        Networks.reform(net);
    }

    public long count(String key, Object value) {
        return Agents.values().stream().filter(ag->ag.get(key) == value).count();
    }

    public long count(Map<String, Object> kvs) {
        return Agents.values().stream().filter(ag -> ag.isCompatible(kvs)).count();
    }


    public long count() {
        return Agents.size();
    }

    @Override
    public JSONObject toJSON() {
        // todo
        return null;
    }

    public JSONArray getSnapshot() {
        JSONArray js = new JSONArray();
        for (T ag: Agents.values()) {
            js.put(ag.toData());
        }
        return js;
    }
}
