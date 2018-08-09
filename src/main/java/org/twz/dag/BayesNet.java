package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.dag.loci.*;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNet implements AdapterJSONObject {
    private final String Name;
    private boolean frozen;
    private DiGraph<Loci> DAG;

    public BayesNet(String name) {
        Name = name;
        DAG = new DiGraph<>();
        frozen=false;
    }

    public BayesNet(JSONObject js) {
        this(js.getString("Name"));

        JSONArray nodes = js.getJSONArray("Nodes");
        for (int i = 0; i < nodes.length(); i++) {
            appendLoci(nodes.getJSONObject(i));
        }
        complete();
    }

    public String getName() {
        return Name;
    }

    public DiGraph<Loci> getDAG() {
        return DAG;
    }

    public void appendLoci(String name, Loci loci) {
        if (isFrozen()) return;
        DAG.addNode(name, loci);
        List<String> pas = loci.getParents();
        try {
            for (String pa : pas) {
                if (!DAG.has(pa)) {
                    DAG.addNode(pa, new ExoValueLoci(pa));
                }
                DAG.addEdge(pa, name);
            }
        } catch (NullPointerException ignored) {

        }
    }

    public void appendLoci(String def) {
        def = def.replaceAll("\\s+", "");

        if (def.contains("=")) {
            String[] opt = def.split("=");
            Loci lo;
            try {
                lo = new ValueLoci(opt[0], opt[1]);
            } catch (ScriptException e) {
                lo = new FunctionLoci(opt[0], opt[1]);
            }
            appendLoci(opt[0], lo);
        } else if (def.contains("~")) {
            String[] opt = def.split("~");
            appendLoci(opt[0], new DistributionLoci(opt[0], opt[1]));
        } else {
            appendLoci(def, new ExoValueLoci(def));
        }
    }

    public void appendLoci(JSONObject loc) {
        String nd = loc.getString("Name");

        Loci loci;
        switch (loc.getString("Type")) {
            case "Distribution":
                loci = new DistributionLoci(nd, loc.getString("Def"), toList(loc.getJSONArray("Parents")));
                break;
            case "Function":
                loci = new FunctionLoci(nd, loc.getString("Def"), toList(loc.getJSONArray("Parents")));
                break;
            case "Value":
                loci = new ValueLoci(nd, loc.getDouble("Def"));
                break;
            case "Pseudo":
                loci = new PseudoLoci(nd, loc.getString("Def"));
                break;
            default:
                loci = new ExoValueLoci(nd);
                break;
        }
        appendLoci(nd, loci);
    }

    public Loci getLoci(String loc) {
        return DAG.getNode(loc);
    }

    public Gene sample(Map<String, Double> inp) {
        Loci loci;
        Gene gene = new Gene(inp);
        for (String s : getOrder()) {
            loci = DAG.getNode(s);
            if (!(loci instanceof ExoValueLoci)) {
                loci.fill(gene);
                gene.addLogPriorProb(loci.evaluate(gene.getLocus()));
            }
        }
        return gene;
    }



    private List<String> toList(JSONArray ja) {
        List<String> l = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            l.add(ja.getString(i));
        }
        return l;
    }

    public List<String> getOrder() {
        try {
            return DAG.getOrder();
        } catch (InvalidPropertiesFormatException e) {
            return null;
        }
    }

    public void complete() {
        frozen = true;
    }

    private void defrost() {
        frozen = false;
    }

    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject(), nodes = new JSONObject();
        js.put("Name", getName());


        for (String s : getOrder()) {
            nodes.put(s, DAG.getNode(s).toJSON());
        }

        js.put("Nodes", nodes);
        js.put("Order", getOrder());

        return js;
    }


    public void print() {
        System.out.println("Nodes:");

        for (String loci: getOrder()) {
            System.out.println("\t"+DAG.getNode(loci));
        }

        System.out.println("Roots:"+ DAG.getRoots());
        System.out.println("Leaves:"+ DAG.getLeaves());
    }
}
