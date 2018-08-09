package org.twz.dag;

import org.json.JSONObject;
import org.twz.dag.loci.Loci;
import org.twz.dag.util.NodeGroup;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        SGs = new HashMap<>();
        findSGs();
    }

    public String getName() {
        return Name;
    }

    SimulationGroup get(String sg) {
        return SGs.get(sg);
    }

    BayesNet getBN() {
        return BN;
    }

    private Map<String, SimulationGroup> getSGs() {
        return SGs;
    }

    private void findSGs() {
        DiGraph<Loci> g = BN.getDAG();
        toSG(RootNG, g);
    }

    private void toSG(NodeGroup ng, DiGraph<Loci> g) {
        Set<String> listen = new HashSet<>();
        for (String s : ng.getNodes()) {
            listen.addAll(g.getParents(s));
        }
        listen.removeAll(ng.getNodes());

        SimulationGroup sg = new SimulationGroup(ng.getName(), listen, ng.getExo(), ng.getFixed(), ng.getRandom(), ng.getActors());
        sg.Children.addAll(ng.getChildren().stream().map(NodeGroup::getName).collect(Collectors.toSet()));
        SGs.put(ng.getName(), sg);

        ng.getChildren().forEach(d->toSG(d, g));
    }

    public ParameterCore generate(String nickname, Map<String, Double> exo) {
        exo = new HashMap<>(exo);
        return SGs.get(RootSG).generate(nickname, exo, null, true);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("BayesianNetwork", BN.toJSON());
        js.put("Blueprint", "todo"); //todo
        js.put("Root", RootSG);
        return js;
    }

    boolean isHoist() {
        return Hoist;
    }
}
