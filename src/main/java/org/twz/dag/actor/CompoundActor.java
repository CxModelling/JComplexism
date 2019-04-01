package org.twz.dag.actor;

import org.twz.dag.Chromosome;
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
    protected List<String> getParents() {
        return new ArrayList<>(AllParents);
    }

    @Override
    public double sample(Chromosome pas) {
        Map<String, Double> ps = findParentValues(pas);
        for (Loci loci : Flow) {
            ps.put(loci.getName(), loci.sample(ps));
        }
        return End.sample(ps);
    }

    @Override
    public double sample(Chromosome pas, Map<String, Double> exo) {
        Map<String, Double> ps = findParentValues(pas, exo);
        for (Loci loci : Flow) {
            ps.put(loci.getName(), loci.sample(ps));
        }
        return End.sample(ps);
    }

    public void fillAll(Chromosome chromosome) {
        chromosome.getLocus().putAll(findParentValues(chromosome));
        chromosome.put(Field, End.sample(chromosome));
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
