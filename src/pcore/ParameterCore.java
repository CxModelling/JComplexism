package pcore;

import pcore.distribution.IDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public class ParameterCore extends Gene {

    private Map<String, IDistribution> Distributions;

    public ParameterCore(Map<String, Double> fixed, Map<String, IDistribution> dis) {
        super(fixed);
        Distributions = dis;
    }

    public IDistribution getDistribution(String key) {
        return Distributions.get(key);
    }

    public String toJSON() {
        String sb = "{";
        sb += getLocus().entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(","));
        sb += "}";
        return sb;
    }

    public Gene realiseAll() {
        Map<String, Double> locus = new HashMap<>();
        locus.putAll(this.getLocus());
        for (Map.Entry<String, IDistribution> ent: Distributions.entrySet()) {
            locus.put(ent.getKey(), ent.getValue().sample());
        }
        return new Gene(locus);
    }

    public Gene fixLeaves() {
        Map<String, Double> locus = new HashMap<>();
        locus.putAll(this.getLocus());
        for (Map.Entry<String, IDistribution> ent: Distributions.entrySet()) {
            locus.put(ent.getKey(), ent.getValue().getMean());
        }
        return new Gene(locus);
    }

    public ParameterCore clone() {
        return (ParameterCore) super.clone();
    }

    public List<String> findDifference(ParameterCore pc) {
        List<String> diff = new ArrayList<>();
        for (Map.Entry<String, IDistribution> ent: Distributions.entrySet()) {
            if (!pc.Distributions.containsKey(ent.getKey())) {
                diff.add(ent.getKey());
            } else if (!pc.getDistribution(ent.getKey()).getName().equals(ent.getValue().getName())) {
                diff.add(ent.getKey());
            }
        }
        for (Map.Entry<String, Double> ent: getLocus().entrySet()) {
            if (!pc.getLocus().containsKey(ent.getKey())) {
                diff.add(ent.getKey());
            } else if (pc.get(ent.getKey()) != ent.getValue()) {
                diff.add(ent.getKey());
            }
        }
        return diff;
    }

}
