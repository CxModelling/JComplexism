package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.loci.*;
import org.twz.dag.util.NodeGroup;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNet implements AdapterJSONObject {
    private final String Name;
    private boolean frozen;
    private DiGraph<Loci> DAG;
    private List<String> Order;

    public BayesNet(String name) {
        Name = name;
        DAG = new DiGraph<>();
        frozen=false;
    }

    public BayesNet(JSONObject js) throws ScriptException, JSONException {
        this(js.getString("Name"));

        JSONArray nodes = js.getJSONArray("Nodes");
        for (int i = 0; i < nodes.length(); i++) {
            appendLoci(nodes.getJSONObject(i));
        }
        try {
            complete();
        } catch (InvalidPropertiesFormatException e) {
            throw new ScriptException("Cyclic sub-graph found");
        }
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
        // todo "\\w+="
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

    private void appendLoci(JSONObject loc) throws JSONException {
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

    public double evaluate(Gene gene) {
        Loci loci;
        double li = 0;
        for (String s : getOrder()) {
            loci = DAG.getNode(s);
            li += loci.evaluate(gene);
        }
        gene.setLogPriorProb(li);
        return li;
    }


    private List<String> toList(JSONArray ja) throws JSONException {
        List<String> l = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            l.add(ja.getString(i));
        }
        return l;
    }

    public List<String> getOrder() {
        if (isFrozen()) {
            return Order;
        } else {
            try {
                return DAG.getOrder();
            } catch (InvalidPropertiesFormatException e) {
                return null;
            }
        }
    }

    public void complete() throws InvalidPropertiesFormatException {
        frozen = true;
        Order = DAG.getOrder();
    }

    private void defrost() {
        frozen = false;
        Order = null;
    }

    public SimulationCore toSimulationCore() {
        NodeGroup ng = new NodeGroup("root", new String[]{});
        ng.allocateNodes(this);
        return new SimulationCore(this, ng, true);
    }

    public SimulationCore toSimulationCore(NodeGroup root, boolean hoist) {
        root.allocateNodes(this);
        return new SimulationCore(this, root, hoist);
    }

    public SimulationCore toSimulationCore(NodeGroup root, String[] random, String[] out, boolean hoist) {
        root.allocateNodes(this, new HashSet<>(Arrays.asList(random)), new HashSet<>(Arrays.asList(out)));
        return new SimulationCore(this, root, hoist);
    }

    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
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


    public static BayesNet buildFromScript(String script) throws ScriptException {
        BayesNet bn;

        String[] row_lines = script.split("\n");

        String pattern = "\\s*PCore\\s+(\\w+)\\s*\\{";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(row_lines[0]);


        if (m.find( )) {
            bn = new BayesNet(m.group(1));
        } else {
            throw new ScriptException("Illegal script");
        }


        String line;
        for (int i = 1; i < row_lines.length; i++) {
            line = row_lines[i].replaceAll("\\s+", "");
            line = line.replaceAll("#\\w*", "");
            if (line.startsWith("}")) {
                break;
            }
            bn.appendLoci(line);
        }
        return bn;
    }
}
