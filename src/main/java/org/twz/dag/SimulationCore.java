package org.twz.dag;

import org.json.JSONObject;
import org.twz.dag.util.NodeGroup;
import org.twz.io.AdapterJSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class SimulationCore implements AdapterJSONObject, Cloneable {
    private String Name, RootSG;
    private BayesNet BN;
    private NodeGroup RootNG;
    private Map<String, SimulationGroup> SGs;
    private boolean Hoist;

    public SimulationCore(BayesNet bn, NodeGroup rootNG, boolean hoist) {
        Name = bn.getName();
        BN = bn;
        RootNG = rootNG;
        RootSG = rootNG.getName();
        Hoist = hoist;
        SGs = getSGs();
    }

    SimulationGroup get(String sg) {
        return SGs.get(sg);
    }

    private Map<String, SimulationGroup> getSGs() {
        return SGs;
    }

    public ParameterCore generate(String nickname, Map<String, Double> exo) {
        exo = new HashMap<>(exo);
        return SGs.get(RootSG).generate(nickname, exo, null, true);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("BayesianNetwork", BN.toJSON());
        js.put("Blueprint", "todo"); //todo
        js.put("Root", RootSG);
        return js;
    }

}
