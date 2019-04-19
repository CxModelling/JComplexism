package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.twz.dag.loci.*;
import org.twz.dag.util.NodeGroup;
import org.twz.dag.util.NodeSet;
import org.twz.datafunction.AbsDataFunction;
import org.twz.datafunction.DataCentre;
import org.twz.exception.IncompleteConditionException;
import org.twz.exception.ScriptException;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 07/08/2018.
 */
public class BayesNet implements AdapterJSONObject {
    private final String Name;
    private boolean frozen;
    private DiGraph<Loci> DAG;
    private List<String> Order;
    private List<String> Roots, RVRoots, Leaves, Exogenous;

    public BayesNet(String name) {
        Name = name;
        DAG = new DiGraph<>();
        Roots = new ArrayList<>();
        RVRoots = new ArrayList<>();
        Leaves = new ArrayList<>();
        Exogenous = new ArrayList<>();
        frozen = false;
    }

    public BayesNet(JSONObject js) throws ScriptException, JSONException {
        this(js.getString("Name"));

        JSONArray nodes = js.getJSONArray("Nodes");
        for (int i = 0; i < nodes.length(); i++) {
            appendLoci(nodes.getJSONObject(i));
        }

        if (!checkAcyclic()) {
            throw new ScriptException("Cyclic sub-graph found");
        }
        complete();
    }

    public String getName() {
        return Name;
    }

    public DiGraph<Loci> getDAG() {
        return DAG;
    }

    public List<String> getRoots() {
        if (isFrozen()) {
            return Roots;
        } else {
            return DAG.getRoots();
        }
    }

    public List<String> getRVRoots() {
        if (isFrozen()) {
            return RVRoots;
        } else {
            return findRVRoots();
        }
    }

    public List<String> getLeaves() {
        if (isFrozen()) {
            return Leaves;
        } else {
            return DAG.getLeaves();
        }
    }

    public List<String> getExogenous() {
        if (isFrozen()) {
            return Exogenous;
        } else {
            return findExogenous();
        }
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
            case "Sampler":
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

    public void bindDataFunctions(Map<String, AbsDataFunction> fns) {
        DataCentre dc = new DataCentre(fns);

        Loci loci;

        for (String s : DAG.getOrder()) {
            loci = DAG.getNode(s);
            if (loci instanceof Bindable) {
                ((Bindable) loci).bindDataCentre(dc);
            }
        }

    }

    public Chromosome sample() {
        Chromosome chromosome = new Chromosome();
        fillAll(chromosome);
        evaluate(chromosome);
        return chromosome;
    }

    public Chromosome sample(Map<String, Double> inp) {
        Chromosome chromosome = new Chromosome(inp);
        fillAll(chromosome);
        evaluate(chromosome);
        return chromosome;
    }

    private void fillAll(Chromosome chromosome) {
        Loci loci;
        for (String s : getOrder()) {
            if (chromosome.has(s)) {
                continue;
            }
            loci = DAG.getNode(s);
            if (!(loci instanceof ExoValueLoci)) {
                try {
                    loci.fill(chromosome);
                } catch (IncompleteConditionException ignored) {

                }
            }
        }
    }

    public void evaluate(Chromosome chromosome) {
        Loci loci;
        double li = 0;
        for (String s : getOrder()) {
            if (chromosome.has(s)) {
                loci = DAG.getNode(s);
                try {
                    li += loci.evaluate(chromosome);
                } catch (IncompleteConditionException ignored) {

                }
            }
        }
        chromosome.setLogPriorProb(li);
    }

    public void impulse(Chromosome chromosome, Map<String, Double> nodes) {
        Set<String> shocked = new HashSet<>();

        for (String s : nodes.keySet()) {
            shocked.addAll(DAG.getDescendants(s));
        }

        double value;
        Loci loci;
        for (String s: getOrder()) {
            if (shocked.contains(s)) {
                value = nodes.getOrDefault(s, Double.NaN);
                if (Double.isNaN(value)) {
                    loci = DAG.getNode(s);
                    if (!(loci instanceof ExoValueLoci)) {
                        try {
                            loci.fill(chromosome);
                        } catch (IncompleteConditionException ignored) {

                        }
                    }
                } else {
                    chromosome.getLocus().put(s, value);
                }
            }
        }
        chromosome.resetProbability();
        evaluate(chromosome);
    }

    public void impulse(Chromosome chromosome, List<String> nodes) {
        Map<String, Double> imp = new HashMap<>();
        for (String node : nodes) {
            imp.put(node, Double.NaN);
        }
        impulse(chromosome, imp);
    }

    public void bindExogenous(Chromosome chromosome, Map<String, Double> exo) {
        Map<String, Double> imp = new HashMap<>();
        for (Map.Entry<String, Double> ent : exo.entrySet()) {
            if (Order.contains(ent.getKey())) {
                imp.put(ent.getKey(), ent.getValue());
            }
        }
        impulse(chromosome, imp);
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
            return DAG.getOrder();
        }
    }

    private boolean checkAcyclic() {
        // todo

        return true;
    }

    public void complete() {
        frozen = true;
        Order = DAG.getOrder();
        Roots = DAG.getRoots();
        Leaves = DAG.getLeaves();
        Exogenous = findExogenous();
        RVRoots = findRVRoots();
    }

    private void defrost() {
        frozen = false;
        Order = null;
        Roots = null;
        Leaves = null;
        Exogenous = null;
        RVRoots = null;
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

    public SimulationCore toSimulationCoreNoOut(NodeGroup root, boolean hoist) {
        root.allocateNodes(this, new HashSet<>(), new HashSet<>());
        return new SimulationCore(this, root, hoist);
    }

    public ParameterModel toParameterModel(NodeSet ns) {
        ns.injectGraph(this);
        return new ParameterModel(this, ns);
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

    public List<String> findRVRoots() {
        List<String> rvr = new ArrayList<>();
        Loci loci;
        for (String s : getOrder()) {
            loci = getLoci(s);
            if (loci instanceof DistributionLoci) {
                boolean test = true;
                for (Loci parent : DAG.getAncestorNodes(s)) {
                    if (parent instanceof DistributionLoci) {
                        test = false;
                        break;
                    }
                }
                if (test) {
                    rvr.add(s);
                }
            }
        }
        return rvr;
    }

    private List<String> findExogenous() {
        return getRoots().stream()
                .filter(n -> getLoci(n) instanceof ExoValueLoci)
                .collect(Collectors.toList());
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
            line = line.split("#")[0];


            if (line.startsWith("}")) {
                break;
            }
            if (line.isEmpty()) continue;
            bn.appendLoci(line);
        }
        return bn;
    }
}
