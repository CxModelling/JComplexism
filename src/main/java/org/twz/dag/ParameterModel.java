package org.twz.dag;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.loci.Loci;
import org.twz.exception.ScriptException;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;
import org.twz.io.FnJSON;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ParameterModel implements AdapterJSONObject {
    private String Name, RootPG;
    private BayesNet BN;
    private NodeSet Root;
    private Map<String, ParameterGroup> PGs;

    ParameterModel(BayesNet bn, NodeSet root) {
        Name = bn.getName();
        BN = bn;
        Root = root;
        RootPG = root.getName();
        PGs = new HashMap<>();
        findPGs();
    }

    public ParameterModel(JSONObject js) throws ScriptException, JSONException {
        this(new BayesNet(js.getJSONObject("BayesianNetwork")),
                new NodeSet(js.getJSONObject("Blueprint")));
    }

    public String getName() {
        return Name;
    }

    ParameterGroup get(String pg) {
        return PGs.get(pg);
    }

    public BayesNet getBN() {
        return BN;
    }

    private void findPGs() {
        DiGraph<Loci> g = BN.getDAG();
        toPG(Root, g);
    }

    private void toPG(NodeSet ns, DiGraph<Loci> g) {
        ParameterGroup pg = new ParameterGroup(ns.getName(), ns.getExoNodes(), ns.getFixedNodes(), ns.getFloatingNodes());
        pg.Children.addAll(ns.getChildren().stream().map(NodeSet::getName).collect(Collectors.toSet()));
        pg.setParameterModel(this);
        PGs.put(ns.getName(), pg);
        ns.getChildren().forEach(d->toPG(d, g));
    }

    public Parameters generate(String nickname, Map<String, Double> exo) {
        exo = new HashMap<>(exo);
        return PGs.get(RootPG).generate(nickname, exo, null);
    }

    public Parameters generate(String nickname) {
        return PGs.get(RootPG).generate(nickname, null, null);
    }

    public Parameters generate(JSONObject js) throws JSONException {
        return generate(js.getString("Name"), FnJSON.toDoubleMap(js.getJSONObject("Values")));
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("BayesianNetwork", BN.toJSON());
        js.put("Blueprint", Root.toJSON());
        js.put("Root", RootPG);
        return js;
    }

}
