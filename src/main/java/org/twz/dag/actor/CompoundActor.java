package org.twz.dag.actor;

import org.twz.dag.Chromosome;
import org.twz.dag.loci.Loci;
import org.twz.exception.IncompleteConditionException;

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
            try {
                AllParents.addAll(loc.getParents());
            } catch (NullPointerException ignored) {

            }
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
    public double sample(Chromosome pas) throws IncompleteConditionException {
        Map<String, Double> ps = findParentValues(pas);
        double v;
        for (Loci loci : Flow) {
            v = pas.getDouble(loci.getName());
            if (Double.isNaN(v)) {
                try {
                    ps.put(loci.getName(), loci.render(ps));
                } catch (org.twz.exception.IncompleteConditionException e) {
                    e.printStackTrace();
                }
            } else {
                ps.put(loci.getName(), v);
            }
        }
        return End.render(ps);
    }

    @Override
    public double sample(Chromosome pas, Map<String, Double> exo) throws IncompleteConditionException {
        Map<String, Double> ps = findParentValues(pas, exo);
        for (Loci loci : Flow) {
            try {
                ps.put(loci.getName(), loci.render(ps));
            } catch (org.twz.exception.IncompleteConditionException e) {
                e.printStackTrace();
            }
        }
        return End.render(ps);

    }

    public void fillAll(Chromosome chromosome) {
        chromosome.getLocus().putAll(findParentValues(chromosome));
        try {
            chromosome.put(Field, End.render(chromosome));
        } catch (org.twz.exception.IncompleteConditionException e) {
            e.printStackTrace();
        }
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
