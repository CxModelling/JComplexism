package org.twz.dag;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.loci.ExoValueLoci;
import org.twz.dag.loci.Loci;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;
import org.twz.io.FnJSON;

import java.util.*;

public class NodeSet implements AdapterJSONObject {
    private final String Name;

    private NodeSet Parent;
    private Set<NodeSet> Children;
    private Set<String> AsFixed, AsFloating;
    private Set<String> Par, Med, Chd, ToCheck;
    private Set<String> ExoNodes, FixedNodes, FloatingNodes;

    public NodeSet(String name, String[] as_fixed, String[] as_float) {
        Name = name;
        Parent = null;
        Children = new HashSet<>();
        AsFloating = (as_float != null)? new HashSet<>(Arrays.asList(as_float)): new HashSet<>();
        AsFixed = (as_fixed != null)? new HashSet<>(Arrays.asList(as_fixed)): new HashSet<>();
        AsFixed.removeAll(AsFloating);

        Par = new HashSet<>();
        Med = new HashSet<>();
        Chd = new HashSet<>();
        ToCheck = new HashSet<>();

        ExoNodes = new HashSet<>();
        FixedNodes = new HashSet<>();
        FloatingNodes = new HashSet<>();
    }

    public NodeSet(String name, String[] fixed) {
        this(name, fixed, null);
    }

    public NodeSet(JSONObject js) throws JSONException {
        this(js.getString("Name"), null, null);
        AsFixed.addAll(FnJSON.toStringSet(js.getJSONArray("AsFixed")));
        AsFloating.addAll(FnJSON.toStringSet(js.getJSONArray("AsFloating")));
        ExoNodes.addAll(FnJSON.toStringSet(js.getJSONArray("Exo")));
        FixedNodes.addAll(FnJSON.toStringSet(js.getJSONArray("Fixed")));
        FloatingNodes.addAll(FnJSON.toStringSet(js.getJSONArray("Floating")));
    }

    public String getName() {
        return Name;
    }

    private boolean needs(String s) {
        return AsFloating.contains(s) ||
                AsFixed.contains(s) ||
                Med.contains(s) ||
                Chd.contains(s);
    }

    public Set<String> getExoNodes() {
        return ExoNodes;
    }

    public Set<String> getFixedNodes() {
        return FixedNodes;
    }

    public Set<String> getFloatingNodes() {
        return FloatingNodes;
    }

    public void injectGraph(BayesNet bn) {
        collectMediators(bn.getDAG());
        for (NodeSet chd : Children)
            chd.collectMediators(bn.getDAG());

        collectChildNodes(bn.getDAG());
        for (NodeSet chd : Children)
            chd.collectChildNodes(bn.getDAG());
        passDown();

        collectParentNodes(bn.getDAG());
        for (NodeSet chd : Children)
            chd.collectParentNodes(bn.getDAG());

        collectRootNodes(bn.getDAG());

        gatherToCheck();
        for (NodeSet chd : Children)
            chd.gatherToCheck();

        locateTypes(bn.getDAG());
    }

    private void collectMediators(DiGraph<Loci> dag) {
        Set<String> anc = new HashSet<>(), dec = new HashSet<>();
        Med.clear();
        for (String node : AsFloating) {
            anc.addAll(dag.getAncestors(node));
            dec.addAll(dag.getDescendants(node));
        }

        for (String node : AsFixed) {
            anc.addAll(dag.getAncestors(node));
            dec.addAll(dag.getDescendants(node));
        }
        Med.addAll(anc);
        Med.retainAll(dec);

        Med.removeAll(AsFloating);
        Med.removeAll(AsFixed);

    }

    private void collectChildNodes(DiGraph<Loci> dag) {
        Chd.clear();

        for (String node : AsFloating)
            Chd.addAll(dag.getDescendants(node));

        for (String node : AsFixed)
            Chd.addAll(dag.getDescendants(node));

        for (String node : Med)
            Chd.addAll(dag.getDescendants(node));

        Chd.removeAll(AsFixed);
        Chd.removeAll(AsFloating);
        Chd.removeAll(Med);
    }

    private void passDown() {
        if (Children.isEmpty()) return;

        for (NodeSet chd : Children)
            chd.passDown();


        Set<String> to_drop = new HashSet<>();
        for (String s : Chd) {
            for (NodeSet chd : Children) {
                if (chd.needs(s)) {
                    to_drop.add(s);
                    break;
                }
            }
        }
        Chd.removeAll(to_drop);
    }

    private void collectParentNodes(DiGraph<Loci> dag) {
        Par.clear();

        for (String node : AsFloating) {
            Par.addAll(dag.getAncestors(node));
        }

        for (String node : AsFixed) {
            Par.addAll(dag.getAncestors(node));
        }

        for (String node : Med) {
            Par.addAll(dag.getAncestors(node));
        }
        Par.removeAll(AsFixed);
        Par.removeAll(AsFloating);
        Par.removeAll(Med);
        Par.removeAll(Chd);
    }

    private void collectRootNodes(DiGraph<Loci> dag) {
        Loci loc;
        for (String s : dag.getOrder()) {
            if (!findDeeply(s)) {
                loc = dag.getNode(s);
                if (loc instanceof ExoValueLoci) {
                    ExoNodes.add(s);
                } else if (loc.getParents().isEmpty()) {
                    ToCheck.add(s);
                }
            }
        }
    }

    private void gatherToCheck() {
        ToCheck.addAll(Med);
        ToCheck.addAll(Chd);
    }

    private boolean findDeeply(String node) {
        if (AsFixed.contains(node)) return true;
        if (AsFloating.contains(node)) return true;
        if (Med.contains(node)) return true;
        if (Chd.contains(node)) return true;

        for (NodeSet child : Children) {
            if (child.findDeeply(node)) return true;
        }

        return false;
    }

    private boolean isFloating(String s) {
        if (FloatingNodes.contains(s)) return true;
        if (Parent != null)return Parent.isFloating(s);
        return false;
    }

    private void locateTypes(DiGraph<Loci> dag) {
        FloatingNodes.clear();
        FixedNodes.clear();

        Loci loci;
        List<String> pars;
        for (String s : dag.getOrder()) {
            if (AsFloating.contains(s)) {
                FloatingNodes.add(s);
            } else if (AsFixed.contains(s) || ToCheck.contains(s)) {
                loci = dag.getNode(s);
                pars = loci.getParents();
/*                if (pars.isEmpty()) {
                    if (loci instanceof DistributionLoci) {
                        FloatingNodes.add(s);
                    } else {
                        FixedNodes.add(s);
                    }
                }*/
                boolean flo = false;
                for (String par : pars) {
                    flo |= isFloating(par);
                }
                if (flo) {
                    FloatingNodes.add(s);
                } else if (loci instanceof ExoValueLoci){
                    ExoNodes.add(s);
                } else {
                    FixedNodes.add(s);
                }
            }
        }
        ExoNodes.addAll(Par);
        Children.forEach(chd->chd.locateTypes(dag));
    }

    public void appendChild(NodeSet chd) {
        Children.add(chd);
        chd.Parent = this;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("AsFixed", AsFixed);
        js.put("AsFloating", AsFloating);
        js.put("Exo", ExoNodes);
        js.put("Fixed", FixedNodes);
        js.put("Floating", FloatingNodes);

        List<JSONObject> list = new ArrayList<>();
        for (NodeSet Child : Children) {
            JSONObject toJSON = Child.toJSON();
            list.add(toJSON);
        }
        js.put("Children", list);
        return js;
    }

    private void printAll(int i) {
        String h = new String(new char[i]).replace("\0", "  ");
        System.out.println(h + "Node " + Name);

        System.out.println(h + "As Fixed: " + String.join(", ", AsFixed));
        System.out.println(h + "As Float: " + String.join(", ", AsFloating));

        System.out.println(h + "Med     : " + String.join(", ", Med));
        System.out.println(h + "Chd     : " + String.join(", ", Chd));
        System.out.println(h + "To check: " + String.join(", ", ToCheck));

        System.out.println(h + "Exo     : " + String.join(", ", ExoNodes));
        System.out.println(h + "Fix     : " + String.join(", ", FixedNodes));
        System.out.println(h + "Float   : " + String.join(", ", FloatingNodes));

        System.out.println(h + "----");
        Children.forEach(chd->chd.printAll(i + 2));
    }

    public void printAll() {
        printAll(0);
    }

    private void print(int i) {
        String h = new String(new char[i]).replace("\0", "  ");
        System.out.println(h + "Node " + Name);

        System.out.println(h + "Exo     : " + String.join(", ", ExoNodes));
        System.out.println(h + "Fix     : " + String.join(", ", FixedNodes));
        System.out.println(h + "Float   : " + String.join(", ", FloatingNodes));

        System.out.println(h + "----");
        Children.forEach(chd->chd.print(i + 2));
    }

    public void print() {
        print(0);
    }


    public Set<NodeSet> getChildren() {
        return Children;
    }
}
