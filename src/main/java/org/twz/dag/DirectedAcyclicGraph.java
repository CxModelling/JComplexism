package org.twz.dag;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.dag.loci.*;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by TimeWz on 2017/11/4.
 */
public class DirectedAcyclicGraph implements AdapterJSONObject {
    private static int NumDAG = 0;
    private final static HashMap<String, Double> NullCon = new HashMap<>();
    private final String Name;
    private Map<String, Loci> Locus;
    private String[] Order;
    private final int Depth;
    private Map<String, Set<LinkedList<String>>> Pathways;
    private List<String> Roots, Leaves;
    private final JSONObject Source;

    public DirectedAcyclicGraph(String script) throws ScriptException {
        this(scriptToJson(script));
    }

    public DirectedAcyclicGraph(JSONObject js) throws ScriptException {
        Source = js;
        Name = js.getString("Name");
        Depth = js.getInt("Depth");
        restoreLocus(js.getJSONObject("Nodes"), js.getJSONArray("Order"));
        Leaves = Locus.keySet().stream().filter(e->getChildren(e).isEmpty()).collect(Collectors.toList());
        Roots = Locus.keySet().stream().filter(e->getParents(e).isEmpty()).collect(Collectors.toList());
    }

    public String getName() {
        return Name;
    }

    public Loci getLoci(String loci) {
        return Locus.get(loci);
    }

    public String[] getOrder() {
        return Order;
    }

    public int getDepth() {
        return Depth;
    }

    public List<String> getRoots() {
        return Roots;
    }

    public List<String> getLeaves() {
        return Leaves;
    }

    public Map<String, Set<LinkedList<String>>> getPathways() {
        if (Pathways == null) {
            findPathways();
        }
        return Pathways;
    }

    public Set<LinkedList<String>> getPathways(String from, String to) {
        if (Pathways == null) {
            findPathways();
        }
        return Pathways.get(from).stream().filter(s -> s.getLast().equals(to)).collect(Collectors.toSet());
    }

    public Map<String, Double> sample() {
        return sample(NullCon);
    }

    public Map<String, Double> sample(Map<String, Double> cond) {
        Map<String, Double> vs = new HashMap<>();
        for (String loci: Order) {
            vs.put(loci, (cond.containsKey(loci))? cond.get(loci): Locus.get(loci).sample(vs));
        }
        return vs;
    }

    public double evaluate(Map<String, Double> vs) {
        return Locus.values().stream()
                .filter(e->vs.containsKey(e.getName()))
                .mapToDouble(e->e.evaluate(vs))
                .sum();
    }

    public boolean isDistribution(String m) {
        return Locus.get(m) instanceof DistributionLoci;
    }

    public Collection<String> getChildren(String src) {
        return Locus.values().stream()
                .filter(e->e.getParents().contains(src))
                .map(Loci::getName)
                .collect(Collectors.toList());
    }

    public Collection<String> getDescendants(String src) {
        Set<String> des = new HashSet<>();
        getPathways().get(src)
                .forEach(e->des.addAll(e.stream()
                        .filter(v->!v.equals(src))
                        .collect(Collectors.toList())));
        return des;
    }

    public Collection<String> getAscendants(String src) {
        Set<String> as = new HashSet<>();
        for (String nod: getOrder()) {
            if (nod.equals(src))
                break;
            if (getPathways().get(src).stream().anyMatch(e->e.contains(src)))
                as.add(nod);
        }
        return as;
    }

    public Collection<String> getParents(String src) {
        return Locus.get(src).getParents();
    }

    public BayesianModel getBayesianModel(Collection<String> evi) {
        return new BayesianModel(this, evi);
    }

    public SimulationModel getSimulationModel() {
        return new SimulationModel(this);
    }

    public CausalDiagram getCausalDiagram() {
        return new CausalDiagram(this);
    }

    public DirectedAcyclicGraph subgraph(Collection<String> inclusion) {
        // todo
        return null;
    }

    private void findPathways() {
        TreeMap<String, LinkedList<String>> ch = new TreeMap<>();
        for (String k: Order) {
            ch.put(k, new LinkedList<>(getChildren(k)));
        }

        Pathways = new TreeMap<>();
        HashSet<LinkedList<String>> ps, temp0, temp1;
        LinkedList<String> cs, p;

        for (String k: Order) {
            ps = new HashSet<>();
            temp0 = new HashSet<>();
            cs = new LinkedList<>();
            cs.add(k);
            temp0.add(cs);
            temp1 = new HashSet<>();
            while (temp0.size() > 0) {
                for (LinkedList<String> path: temp0) {
                    cs = ch.get(path.getLast());
                    if (cs.size() == 0) {
                        ps.add(path);
                    } else {
                        for (String chd: cs) {
                            p = new LinkedList<>(path);
                            p.add(chd);
                            temp1.add(p);
                        }
                    }
                }
                temp0 = temp1;
                temp1 = new HashSet<>();
            }
            Pathways.put(k, ps);
        }

    }

    public void print() {
        System.out.println("Nodes:");
        for (String loci: Order) {
            System.out.println("\t"+Locus.get(loci));
        }
        System.out.println("Roots:"+ Roots);
        System.out.println("Leaves:"+ Leaves);
    }

    public String toString() {
        String s = "Nodes:\n";
        for (String loci: Order) {
            s += "\t"+Locus.get(loci);
        }
        return s;
    }

    public JSONObject toJSON() {
        return Source;
    }

    private void restoreLocus(JSONObject nodes, JSONArray order) {
        JSONObject loc;
        String nd;

        Locus = new TreeMap<>();
        Order = new String[order.length()];

        for (int i = 0; i < order.length(); i++) {
            nd = order.getString(i);
            Order[i] = nd;
            loc = nodes.getJSONObject(nd);
            Locus.put(nd, restoreLoci(nd, loc));
        }
    }

    private Loci restoreLoci(String nd, JSONObject loc) {
        switch (loc.getString("Type")) {
            case "Distribution":
                return new DistributionLoci(nd, loc.getString("Def"),
                        toList(loc.getJSONArray("Parents")));
            case "Function":
                return new FunctionLoci(nd, loc.getString("Def"),
                        toList(loc.getJSONArray("Parents")));
            case "Value":
                return new ValueLoci(nd, loc.getDouble("Def"));
        }
        return null;
    }

    private List<String> toList(JSONArray ja) {
        List<String> l = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            l.add(ja.getString(i));
        }
        return l;
    }

    public static JSONObject scriptToJson(String script) {
        JSONObject js = new JSONObject(), node;
        HashMap<String, JSONObject> nodes = new HashMap<>();

        String[] lines = script.split("\n"), pair;
        String def, com;
        try {
            for (String line : lines) {
                if (line.contains("#")) {
                    pair = line.split("#", 2);
                    def = pair[0];
                    com = pair[1];
                } else {
                    def = line;
                    com = "";
                }

                def = def.replaceAll("\\s+", "");
                if (def.toUpperCase().indexOf("PCORE") == 0) {
                    def = def.replaceAll("(?i)PCORE", "");
                    js.put("Name", def.split("\\{")[0]);
                } else if (def.contains("~")) {
                    pair = def.split("~");
                    node = new JSONObject();
                    node.put("Type", "Distribution");
                    node.put("Def", pair[1]);
                    node.put("Parents", Loci.parseParents(pair[1]));
                    if (!com.equals("")) node.put("Note", com);
                    nodes.put(pair[0], node);
                } else if (def.contains("=")) {
                    pair = def.split("=");
                    node = new JSONObject();
                    try {
                        double d = Double.parseDouble(pair[1]);
                        node.put("Type", "Value");
                        node.put("Def", d);
                    } catch (Exception e) {
                        node.put("Type", "Function");
                        node.put("Def", pair[1]);
                        node.put("Parents", Loci.parseParents(pair[1]));
                    } finally {
                        if (!com.equals("")) node.put("Note", com);
                        nodes.put(pair[0], node);
                    }
                }
            }
        } catch (Error e) {
            e.printStackTrace();
        }

        ArrayList<String> odn = new ArrayList<>(), temp;
        JSONArray ja;
        int dep = 0;

        while (odn.size() < nodes.size()) {
            temp = new ArrayList<>();
            for (Map.Entry<String, JSONObject> ent : nodes.entrySet()) {
                if (odn.contains(ent.getKey())) continue;
                if (!ent.getValue().has("Parents")) {
                    temp.add(ent.getKey());
                } else {
                    ja = ent.getValue().getJSONArray("Parents");
                    ArrayList<String> pars = new ArrayList<>();
                    for (int i = 0; i < ja.length(); i++) {
                        pars.add(ja.getString(i));
                    }
                    if (odn.containsAll(pars)) {
                        temp.add(ent.getKey());
                    }
                }
            }
            if (temp.isEmpty()) {
                return null;
            }
            odn.addAll(temp);
            dep++;
        }


        if (!js.has("Name")) {
            NumDAG++;
            js.put("Name", "PCore_" + NumDAG);
        }
        js.put("Nodes", nodes);
        js.put("Order", odn);
        js.put("Depth", dep);
        return js;
    }
}
