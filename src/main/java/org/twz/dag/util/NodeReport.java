package org.twz.dag.util;

import org.twz.dag.BayesNet;
import org.twz.dag.loci.*;
import org.twz.graph.DiGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 *
 * Created by TimeWz on 08/08/2018.
 */
public class NodeReport {
    private String Name;
    Set<String> Exo, Fixed, FixedRandom, FixedRandomActor, RandomActor, Random;

    NodeReport(BayesNet bn, NodeGroup ng) {
        Name = ng.getName();
        Exo = new HashSet<>();
        Fixed = new HashSet<>();
        FixedRandom = new HashSet<>();
        FixedRandomActor = new HashSet<>();
        RandomActor = new HashSet<>();
        Random = new HashSet<>();
        analyse(bn, ng);
    }

    public boolean mustBeExo(String node) {
        return Exo.contains(node);
    }

    public boolean canBeFixed(String node) {
        return Exo.contains(node) ||
                Fixed.contains(node) ||
                FixedRandom.contains(node) ||
                FixedRandomActor.contains(node);
    }

    public boolean canBeRandom(String node) {
        return FixedRandom.contains(node) ||
                FixedRandomActor.contains(node) ||
                RandomActor.contains(node) ||
                Random.contains(node);
    }

    public boolean canBeActor(String node) {
        return FixedRandomActor.contains(node) || RandomActor.contains(node);
    }

    private void analyse(BayesNet bn, NodeGroup ng) {
        DiGraph<Loci> g = bn.getDAG();
        List<String> des, ans, exo;
        exo = g.getRoots().stream()
                .filter(n->bn.getLoci(n) instanceof ExoValueLoci)
                .collect(Collectors.toList());

        for (String node : ng.getNodes()) {
            Loci loci = bn.getLoci(node);
            if (loci instanceof ExoValueLoci) {
                Exo.add(node);
            } else if (loci instanceof ValueLoci) {
                Fixed.add(node);
            } else {
                des = g.getDescendants(node);
                ans = g.getAncestors(node);

                if (!des.isEmpty()) {
                    if (ng.getNodes().containsAll(des)) {
                        ans.retainAll(exo);
                        if (ans.isEmpty()) {
                            FixedRandom.add(node);
                        } else {
                            Random.add(node);
                        }
                    } else {
                        Fixed.add(node);
                    }
                } else {
                    if (ans.isEmpty()) {
                        FixedRandomActor.add(node);
                    } else {
                        RandomActor.add(node);
                    }
                }
            }
        }
    }

    public void print(int ind) {
        String h = new String(new char[ind]).replace("\0", "  ");
        System.out.println(h + "Group " + Name);
        Set<String> s = new HashSet<>();
        System.out.println(h + "Exogenous :    " + Exo);
        s.addAll(Fixed);
        s.addAll(FixedRandom);
        s.addAll(FixedRandomActor);
        System.out.println(h + "Can be fixed:  " + s);

        s.clear();
        s.addAll(FixedRandom);
        s.addAll(FixedRandomActor);
        s.addAll(RandomActor);
        s.addAll(Random);
        System.out.println(h + "Can be random: " + s);

        s.clear();
        s.addAll(RandomActor);
        s.addAll(FixedRandomActor);
        System.out.println(h + "Can be actors: " + s);
    }

    public void print() {
        print(0);
    }
}
