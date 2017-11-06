package pcore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/23.
 */
public class CausalDiagram implements IParameterModel {

    private final DirectedAcyclicGraph DAG;

    public CausalDiagram(DirectedAcyclicGraph DAG) {
        this.DAG = DAG;
    }

    // Parents + children and coparents
    public Collection<String> getMarkovBlanket(String src) {
        Set<String> mb = new HashSet<>(), chs = new HashSet<>();
        chs.add(src);
        chs.addAll(DAG.getChildren(src));
        for (String ch: chs) {
            mb.addAll(DAG.getParents(ch));
        }
        mb.addAll(chs);
        mb.remove(src);
        return mb;
    }

    public boolean isIdentifiable(Collection<String> impulse, Collection<String> response) {
        // todo
        return false;
    }

    public boolean getConfounders(Collection<String> impulse, Collection<String> response) {
        // todo
        return false;
    }

    public double calculateDirectEffect(Map<String, Double> standard, Map<String, Double> intervention) {
        // todo
        return 0;
    }

    public double calculateIndirectEffect(Map<String, Double> standard, Map<String, Double> intervention) {
        // todo
        return 0;
    }

    public double calculateTotalEffect(Map<String, Double> standard, Map<String, Double> intervention) {
        // todo
        return 0;
    }

    @Override
    public DirectedAcyclicGraph getDAG() {
        return DAG;
    }
}
