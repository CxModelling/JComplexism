package hgm.abmodel;

import dcore.AbsDCore;
import dcore.State;
import hgm.abmodel.network.AbsNetwork;
import hgm.abmodel.network.NetworkSet;
import hgm.abmodel.trait.ITrait;
import hgm.abmodel.trait.TraitSet;
import utils.json.AdapterJSONObject;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class Population implements AdapterJSONObject {
    private long LastID;
    private String Prefix;
    private AbsDCore DCore;
    private TraitSet Traits;
    private NetworkSet Networks;
    private Map<String, Agent> Agents;

    public Population(AbsDCore dc, String prefix) {
        LastID = 0;
        Prefix = prefix;
        DCore = dc;
        Traits = new TraitSet();
        Networks = new NetworkSet();
        Agents = new LinkedHashMap<>();
    }

    public Population(AbsDCore dc) {
        this(dc, "Ag");
    }

    public void addTrait(ITrait trait) {
        Traits.append(trait);
    }

    public void addNetwork(AbsNetwork net) {
        Networks.append(net);
    }

    public Agent get(String name) {
        return Agents.get(name);
    }

    public List<Agent> addAgents(String state, int n, Map<String, Object> info) {
        List<Agent> ags = breed(state, n, info);
        for (Agent ag: ags) {
            Agents.put(ag.getName(), ag);
            Networks.addAgent(ag);
        }
        return ags;
    }

    public List<Agent> addAgents(String state, int n) {
        return addAgents(state, n, new LinkedHashMap<>());
    }

    public Agent removeAgent(String id) {
        Agent ag;
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

    private List<Agent> breed(String state, int n, Map<String, Object> info) {
        State st = DCore.getWellDefinedStateSpace().get(state);
        long start = LastID + 1;

        List<Agent> ags = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            ags.add(new Agent(Prefix+(start+i), st, Traits.fill(info)));
        }
        LastID += n;
        return ags;
    }


    public double count(State st) {
        return Agents.values().stream().filter(e->e.isa(st)).count();
    }


    @Override
    public JSONObject toJSON() {
        // todo
        return null;
    }

    public JSONArray getTraitArray() {
        JSONArray js = new JSONArray();
        for (Agent ag: Agents.values()) {
            js.put(ag.getInfo());
        }
        return js;
    }

    public JSONArray getSnapshot() {
        JSONArray js = new JSONArray();
        for (Agent ag: Agents.values()) {
            js.put(ag.toSnapshot());
        }
        return js;
    }
}
