package org.twz.dag.actor;

import org.twz.dag.loci.Loci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 08/08/2018.
 */
public class CompoundActor extends SimulationActor {
    private List<Loci> Flow;
    private Loci End;

    public CompoundActor(String field, List<Loci> flow, Loci loci) {
        super(field);
        Flow = new ArrayList<>(flow);
        End = loci;
    }

    @Override
    public double sample(Map<String, Double> pas) {
        pas = new HashMap<>(pas);
        for (Loci loci : Flow) {
            pas.put(loci.getName(), loci.sample(pas));
        }
        return End.sample(pas);
    }

    @Override
    public String toString() {
        return Field + " (" +
                Flow.stream().map(Loci::getDefinition)
                        .collect(Collectors.joining("|")) + ")";
    }


}
