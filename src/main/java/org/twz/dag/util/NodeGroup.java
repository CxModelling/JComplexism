package org.twz.dag.util;

import org.twz.dag.BayesNet;
import org.twz.dag.loci.Loci;
import org.twz.graph.DiGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NodeGroup is used to analysis relation of nodes in a BayesNet
 * Created by TimeWz on 07/08/2018.
 */
public class NodeGroup {
    private String Name;
    private Set<NodeGroup> Children;
    private Set<String> Nodes, Exo, Fixed, Random, Actors;

    public NodeGroup(String name, String[] nodes) {
        Name = name;
        Children = new HashSet<>();
        Nodes = new HashSet<>(Arrays.asList(nodes));
        Exo = new HashSet<>();
        Fixed = new HashSet<>();
        Random = new HashSet<>();
        Actors = new HashSet<>();
    }

    public void appendChildren(NodeGroup ng) {
        Children.add(ng);
    }

    private void push(String node) {
        Nodes.add(node);
    }

    private void pop(String node) {
        Nodes.remove(node);
    }

    private boolean needs(String node, DiGraph<Loci> g) {
        for (String s : Nodes) {
            if (g.getAncestors(s).contains(node)) return true;
            if (g.getDescendants(s).contains(node)) return true;
        }
        return Children.stream().anyMatch(ch->ch.needs(node, g));
    }

    private boolean canBePassedDown(String node, DiGraph<Loci> g) {
        List<String> des = g.getDescendants(node);
        return !Nodes.stream().anyMatch(des::contains);
    }

    private void passDown(String node, DiGraph<Loci> g) {
        push(node);
        if (!canBePassedDown(node, g)) return;

        List<NodeGroup> needed = Children.stream()
                .filter(ch->ch.needs(node, g))
                .collect(Collectors.toList());

        if (needed.size() == 1) {
            pop(node);
            needed.get(0).passDown(node, g);
        }
    }

    private boolean canBeRaisedUp(String node, DiGraph<Loci> g) {
        List<String> anc = g.getAncestors(node);
        anc.retainAll(Nodes);
        return anc.isEmpty();
    }

    private void raiseUp(String node, DiGraph<Loci> g) {
        for (NodeGroup child : Children) {
            if (child.has(node)) {
                child.raiseUp(node, g);
                if (child.canBeRaisedUp(node, g)) {
                    child.pop(node);
                    push(node);
                    return;
                }
            }
        }
    }

    private boolean has(String node) {
        return Nodes.contains(node) || Children.stream().anyMatch(ch -> ch.has(node));
    }

    public String getName() {
        return Name;
    }

    public Set<NodeGroup> getChildren() {
        return Children;
    }

    public Set<String> getNodes() {
        return Nodes;
    }

    public Set<String> getExo() {
        return Exo;
    }

    public Set<String> getFixed() {
        return Fixed;
    }

    public Set<String> getRandom() {
        return Random;
    }

    public Set<String> getActors() {
        return Actors;
    }

    private Set<String> getAllNodes() {
        Set<String> fixed = new HashSet<>(Nodes);
        for (NodeGroup child : Children) {
            fixed.addAll(child.getAllNodes());
        }
        return fixed;
    }

    public void allocateNodes(BayesNet bn) {
        allocateNodes(bn, new HashSet<>(), new HashSet<>(bn.getDAG().getLeaves()));
    }

    public void allocateNodes(BayesNet bn, Set<String> random, Set<String> out) {
        NodeReport nr = new NodeReport(bn, this);
        Exo.addAll(nr.Exo);
        Fixed.addAll(nr.Fixed);
        Random.addAll(nr.Random);

        for (String s : nr.FixedRandom) {
            if (random.contains(s)) {
                Random.add(s);
            } else {
                Fixed.add(s);
            }
        }

        for (String s : nr.FixedRandomActor) {
            if (out.contains(s)) {
                Actors.add(s);
            } else if (random.contains(s)) {
                Random.add(s);
            } else {
                Fixed.add(s);
            }
        }

        for (String s : nr.RandomActor) {
            if (out.contains(s)) {
                Actors.add(s);
            } else {
                Random.add(s);
            }
        }

        Children.forEach(n->n.allocateNodes(bn, random, out));
    }


    @Override
    public String toString() {
        return "NodeGroup{" +
                "Name='" + Name +
                ", Children=" + Children.stream().map(NodeGroup::getName).collect(Collectors.joining(", ")) +
                ", Nodes=" + Nodes.stream().collect(Collectors.joining(", ")) +
                '}';
    }

    private void analyseTypes(BayesNet bn, int i) {
        NodeReport nr = new NodeReport(bn, this);
        nr.print(i);
        Children.forEach(n->n.analyseTypes(bn, i+1));
    }

    public void analyseTypes(BayesNet bn) {
        analyseTypes(bn, 0);
    }

    public void print() {
        System.out.println(toString());
        Children.forEach(NodeGroup::print);
    }

    public void printBlueprint() {
        System.out.println("NodeGroup{" +
                "Name='" + Name +
                ", Exo=" + Exo.stream().collect(Collectors.joining(", ")) +
                ", Fixed=" + Fixed.stream().collect(Collectors.joining(", ")) +
                ", Random=" + Random.stream().collect(Collectors.joining(", ")) +
                ", Actor=" + Actors.stream().collect(Collectors.joining(", ")) +
                '}');
        Children.forEach(NodeGroup::printBlueprint);
    }

    public static void form_hierarchy(BayesNet bn, NodeGroup root) {
        Set<String> all_fixed = root.getAllNodes();
        List<String> all_floated = bn.getOrder();
        all_floated.removeAll(all_fixed);
        Collections.reverse(all_floated);
        all_floated.forEach(node->root.passDown(node, bn.getDAG()));
        Collections.reverse(all_floated);
        all_floated.forEach(node->root.raiseUp(node, bn.getDAG()));
    }




}
