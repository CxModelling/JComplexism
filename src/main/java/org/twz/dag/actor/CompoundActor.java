package org.twz.dag.actor;

import org.twz.dag.Gene;
import org.twz.dag.loci.Loci;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class CompoundActor extends SimulationActor {
    private List<Loci> Flow;
    private Loci End;
    private Set<String> AllParents;

    public CompoundActor(String field, List<Loci> flow, Loci loci) {
        super(field);
        Flow = new ArrayList<>(flow);
        End = loci;
        AllParents = new HashSet<>();
        for (Loci loc: Flow) {
            AllParents.addAll(loc.getParents());
        }
        AllParents.addAll(End.getParents());
        for (Loci loc: Flow) {
            AllParents.remove(loc.getName());
        }
    }

    @Override
    public double sample(Gene pas) {
        Map<String, Double> ps = new HashMap<>();
        for (Loci loci : Flow) {
            ps.put(loci.getName(), loci.sample(pas));
        }
        End.getParents().stream().filter(p->!ps.containsKey(p)).forEach(p->ps.put(p, pas.get(p)));
        return End.sample(pas);
    }

    @Override
    public double sample(Gene pas, Map<String, Double> exo) {
        Map<String, Double> ps = new HashMap<>();
        for (String pa: AllParents) {
            try {
                ps.put(pa, pas.get(pa));
            } catch (NullPointerException e) {
                ps.put(pa, exo.get(pa));
            }
        }

        for (Loci loci: Flow) {
            ps.put(loci.getName(), loci.sample(ps));
        }
        return End.sample(ps);
    }

    @Override
    public void fill(Gene pas) {
        for (Loci loci : Flow) {
            pas.put(loci.getName(), loci.sample(pas));
        }
        End.fill(pas);
    }

    @Override
    public void fill(Gene pas, Map<String, Double> exo) {

    }

    @Override
    public String toString() {
        List<Loci> rev = new ArrayList<>(Flow);
        Collections.reverse(rev);
        return End.getDefinition() + "|" +
                rev.stream().map(Loci::getDefinition)
                        .collect(Collectors.joining("|"));
    }


}
