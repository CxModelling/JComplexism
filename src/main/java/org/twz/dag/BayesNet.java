package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.twz.dag.loci.*;
import org.twz.datafunction.AbsDataFunction;
import org.twz.datafunction.DataCentre;
import org.twz.exception.IncompleteConditionException;
import org.twz.exception.ScriptException;
import org.twz.exception.ValidationException;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;
import org.twz.io.FnJSON;

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
    private List<String> Roots, RVRoots, Leaves, RVLeaves, Exogenous;

    public BayesNet(String name) {
        Name = name;
        DAG = new DiGraph<>();
        Roots = new ArrayList<>();
        RVRoots = new ArrayList<>();
        Leaves = new ArrayList<>();
        RVLeaves = new ArrayList<>();
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

    public List<String> getRVLeaves() {
        if (isFrozen()) {
            return RVLeaves;
        } else {
            return findRVLeaves();
        }
    }

    public List<String> getExogenous() {
        if (isFrozen()) {
            return Exogenous;
        } else {
            return findExogenous();
        }
    }

    private void appendLoci(String name, Loci loci) {
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

    private void appendLoci(JSONObject loc) throws JSONException {
        String nd = loc.getString("Name");

        Loci loci;
        switch (loc.getString("Type")) {
            case "Sampler":
                loci = new DistributionLoci(nd, loc.getString("Def"),
                        FnJSON.toStringList(loc.getJSONArray("Parents")));
                break;
            case "Function":
                loci = new FunctionLoci(nd, loc.getString("Def"),
                        FnJSON.toStringList(loc.getJSONArray("Parents")));
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

        bindDataCentre(dc);
    }

    public void bindDataCentre(DataCentre dc) {
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

    public void fillAll(Chromosome chromosome) {
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
                    li += loci.evaluate(chromosome);;
                } catch (IncompleteConditionException | InstantiationError ignored) {
                    chromosome.setLogPriorProb(Double.NEGATIVE_INFINITY);
                    return;
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

    public void join(BayesNet bn) {
        this.defrost();
        Loci loci;
        for (String node : bn.getOrder()) {
            loci = bn.getLoci(node);
            if (DAG.has(node) & loci instanceof ExoValueLoci) {
                continue;
            }
            appendLoci(node, loci);
        }
        complete();
    }

    public List<String> getOrder() {
        if (isFrozen()) {
            return Order;
        } else {
            return DAG.getOrder();
        }
    }

    private boolean checkAcyclic() {
        return DAG.isAcyclic();
    }

    public void complete() {
        frozen = true;
        Order = DAG.getOrder();
        Roots = DAG.getRoots();
        Leaves = DAG.getLeaves();
        Exogenous = findExogenous();
        RVRoots = findRVRoots();
        RVLeaves = findRVLeaves();
    }

    private void defrost() {
        frozen = false;
        Order = null;
        Roots = null;
        Leaves = null;
        Exogenous = null;
        RVRoots = null;
        RVLeaves = null;
    }

    public ParameterModel toParameterModel(NodeSet ns) throws ValidationException {
        ns.injectGraph(this);
        return new ParameterModel(this, ns);
    }

    public ParameterModel toParameterModel() throws ValidationException {
        NodeSet ns = new NodeSet(getName(), new String[0], getRVLeaves().toArray(new String[0]));
        return toParameterModel(ns);
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

    private List<String> findRVRoots() {
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

    private List<String> findRVLeaves() {
        List<String> rvl = new ArrayList<>();
        Loci loci;
        for (String s : getOrder()) {
            loci = getLoci(s);
            if (loci instanceof DistributionLoci) {
                if (DAG.getDescendantNodes(s).isEmpty()) {
                    rvl.add(s);
                }
            }
        }
        return rvl;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PCore " + Name + " {\n");
        Loci loci;
        for (String s : getOrder()) {
            loci = getLoci(s);
            sb.append("\t").append(loci.getDefinition()).append("\n");
        }
        sb.append("}");
        return sb.toString();
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

    public static BayesNet merge(String newName, BayesNet x, BayesNet y) {
        BayesNet bn = new BayesNet(newName);

        Loci loci;

        for (String node : x.getOrder()) {
            loci = x.getLoci(node);
            bn.appendLoci(loci.getDefinition());
        }

        for (String node : y.getOrder()) {
            loci = y.getLoci(node);
            if (x.DAG.has(node) & loci instanceof ExoValueLoci) {
                continue;
            }
            bn.appendLoci(loci.getDefinition());
        }
        bn.complete();
        return bn;
    }

}
