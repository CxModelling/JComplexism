package org.twz.dag.util;

import org.twz.dag.BayesNet;
import org.twz.dag.loci.Loci;
import org.twz.graph.DiGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by TimeWz on 07/08/2018.
 */
public class NodeGroup {
    private String Name;
    private List<NodeGroup> Children;
    private List<String> Nodes, Fixed, Random, Actors;

    public NodeGroup(String name, String[] fixed) {
        Name = name;
        Children = new ArrayList<>();
        Nodes = new ArrayList<>(Arrays.asList(fixed));
        Fixed = new ArrayList<>(Arrays.asList(fixed));
        Random = new ArrayList<>();
        Actors = new ArrayList<>();
    }

    public void appendChildren(NodeGroup ng) {
        Children.add(ng);
    }

    void push(String node) {
        Nodes.add(node);
    }

    void pop(String node) {
        Nodes.remove(node);
    }

    boolean needs(String node, DiGraph<Loci> g) {
        for (String s : Nodes) {
            if (g.getAncestors(s).contains(node)) return true;
            if (g.getDescendants(s).contains(node)) return true;
        }
        return Children.stream().anyMatch(ch->ch.needs(node, g));
    }

    boolean canBePassedDown(String node, DiGraph<Loci> g) {
        List<String> des = g.getDescendants(node);
        return !Nodes.stream().anyMatch(des::contains);
    }

    void passDown(String node, DiGraph<Loci> g) {
        push(node);
        if (!canBePassedDown(node, g)) return;

        List<NodeGroup> needed = Children.stream().filter(ch->ch.needs(node, g)).collect(Collectors.toList());

        if (needed.size() == 1) {
            pop(node);
            needed.get(0).passDown(node, g);
        }
    }

    boolean canBeRaisedUp(String node, DiGraph<Loci> g) {
        List<String> anc = g.getAncestors(node);
        return !Nodes.stream().anyMatch(anc::contains);
    }

    void raiseUp(String node, DiGraph<Loci> g) {
        for (NodeGroup child : Children) {
            if (!child.has(node)) continue;
            if (child.canBeRaisedUp(node, g)) {
                child.raiseUp(node, g);
                child.pop(node);
                push(node);
            }
        }
    }

    boolean has(String node) {
        if (Nodes.contains(node)) return true;
        return Children.stream().anyMatch(ch->ch.has(node));
    }

    public String getName() {
        return Name;
    }

    public List<NodeGroup> getChildren() {
        return Children;
    }

    public List<String> getNodes() {
        return Nodes;
    }

    public List<String> getFixed() {
        return Fixed;
    }

    public List<String> getRandom() {
        return Random;
    }

    public List<String> getActors() {
        return Actors;
    }

    public Set<String> getAllFixed() {
        Set<String> fixed = new HashSet<>(Fixed);
        for (NodeGroup child : Children) {
            fixed.addAll(child.getAllFixed());
        }
        return fixed;
    }

    @Override
    public String toString() {
        return "NodeGroup{" +
                "Name='" + Name +
                ", Children=" + Children.stream().map(NodeGroup::getName).collect(Collectors.joining(", ")) +
                ", Nodes=" + Nodes.stream().collect(Collectors.joining(", ")) +
                '}';
    }

    public void print() {
        System.out.println(toString());
        Children.forEach(NodeGroup::print);
    }

    public static void form_hierarchy(BayesNet bn, NodeGroup root) {
        Set<String> all_fixed = root.getAllFixed();
        List<String> all_floated = bn.getOrder().stream().filter(s -> !all_fixed.contains(s)).collect(Collectors.toList());
        Collections.reverse(all_floated);
        all_floated.forEach(node->root.passDown(node, bn.getDAG()));
        root.print();
        Collections.reverse(all_floated);
        all_floated.forEach(node->root.raiseUp(node, bn.getDAG()));
    }
}
