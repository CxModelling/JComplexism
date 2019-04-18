package org.twz.dag.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.BayesNet;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.ExoValueLoci;
import org.twz.dag.loci.Loci;
import org.twz.graph.DiGraph;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class NodeSet implements AdapterJSONObject {

    /*
    1. Collect min graph
        1. All mediators
        2. if root, all parents
        3. if leaf, all children
        4. children if none of children sets need
    2.
     */

    private final String Name;

    private Set<NodeSet> Children;
    private Set<String> AsFixed, AsFloat;
    private Set<String> Med, Chd, ToCheck;
    private Set<String> ExoNodes, FixedNodes, FloatNodes;

    public NodeSet(String name, String[] as_fixed, String[] as_float) {
        Name = name;
        Children = new HashSet<>();
        AsFloat = (as_float != null)? new HashSet<>(Arrays.asList(as_float)): new HashSet<>();
        AsFixed = (as_fixed != null)? new HashSet<>(Arrays.asList(as_fixed)): new HashSet<>();
        AsFixed.removeAll(AsFloat);
        Med = new HashSet<>();
        Chd = new HashSet<>();
        ToCheck = new HashSet<>();

        ExoNodes = new HashSet<>();
        FixedNodes = new HashSet<>();
        FloatNodes = new HashSet<>();
    }

    public NodeSet(String name, String[] fixed) {
        this(name, fixed, null);
    }

    boolean has(String s) {
        return AsFixed.contains(s) || AsFloat.contains(s) || Med.contains(s);
    }

    boolean needs(String s) {
        return AsFloat.contains(s) ||
                AsFixed.contains(s) ||
                Med.contains(s) ||
                Chd.contains(s);
    }

    public void injectGraph(BayesNet bn) {
        collectMediators(bn.getDAG());
        for (NodeSet chd : Children) {
            chd.collectMediators(bn.getDAG());
        }

        collectChildNodes(bn.getDAG());
        for (NodeSet chd : Children) {
            chd.collectChildNodes(bn.getDAG());
        }
        passDown();

        collectRootNodes(bn.getDAG());

        gatherToCheck();
        for (NodeSet chd : Children) {
            chd.gatherToCheck();
        }
        locateTypes(bn.getDAG());
    }

    private void collectMediators(DiGraph<Loci> dag) {
        Set<String> anc = new HashSet<>(), dec = new HashSet<>();
        Med.clear();
        for (String node : AsFloat) {
            anc.addAll(dag.getAncestors(node));
            dec.addAll(dag.getDescendants(node));
        }

        for (String node : AsFixed) {
            anc.addAll(dag.getAncestors(node));
            dec.addAll(dag.getDescendants(node));
        }
        Med.addAll(anc);
        Med.retainAll(dec);

        Med.removeAll(AsFloat);
        Med.removeAll(AsFixed);

    }


    private void collectChildNodes(DiGraph<Loci> dag) {
        Chd.clear();

        for (String node : AsFloat) {
            Chd.addAll(dag.getDescendants(node));
        }

        for (String node : AsFixed) {
            Chd.addAll(dag.getDescendants(node));
        }

        for (String node : Med) {
            Chd.addAll(dag.getDescendants(node));
        }
        Chd.removeAll(AsFixed);
        Chd.removeAll(AsFloat);
        Chd.removeAll(Med);
    }

    private void passDown() {
        if (Children.isEmpty()) return;

        for (NodeSet chd : Children) {
            chd.passDown();
        }

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
        if (AsFloat.contains(node)) return true;
        if (Med.contains(node)) return true;
        if (Chd.contains(node)) return true;

        for (NodeSet child : Children) {
            if (child.findDeeply(node)) return true;
        }

        return false;
    }

    private void locateTypes(DiGraph<Loci> dag) {
        FloatNodes.clear();
        FixedNodes.clear();

        Loci loci;
        List<String> pars;
        for (String s : dag.getOrder()) {
            if (AsFloat.contains(s)) {
                FloatNodes.add(s);
            } else if (AsFixed.contains(s)) {
                FixedNodes.add(s);
            } else if (ToCheck.contains(s)) {
                loci = dag.getNode(s);
                pars = loci.getParents();
                if (pars.isEmpty()) {
                    if (loci instanceof DistributionLoci) {
                        FloatNodes.add(s);
                    } else {
                        FixedNodes.add(s);
                    }
                }
                boolean flo = false;
                for (String par : pars) {
                    if (FloatNodes.contains(par)) {
                        flo = true;
                    }
                }
                if (flo) {
                    FloatNodes.add(s);
                } else {
                    FixedNodes.add(s);
                }
            }
        }

        Children.forEach(chd->chd.locateTypes(dag));
    }

    public void appendChild(NodeSet chd) {
        Children.add(chd);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        return null;
    }

    private void print(int i) {
        String h = new String(new char[i]).replace("\0", "  ");
        System.out.println(h + "Node " + Name);

        System.out.println(h + "As Fixed: " + String.join(", ", AsFixed));
        System.out.println(h + "As Float: " + String.join(", ", AsFloat));

        System.out.println(h + "Med     : " + String.join(", ", Med));
        System.out.println(h + "Chd     : " + String.join(", ", Chd));
        System.out.println(h + "To check: " + String.join(", ", ToCheck));

        System.out.println(h + "Exo     : " + String.join(", ", ExoNodes));
        System.out.println(h + "Fix     : " + String.join(", ", FixedNodes));
        System.out.println(h + "Float   : " + String.join(", ", FloatNodes));

        System.out.println(h + "----");
        Children.forEach(chd->chd.print(i + 2));
    }

    public void print() {
        print(0);
    }
}
