package pcore.loci;

import org.json.JSONObject;
import pcore.Gene;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/4/16.
 */
public abstract class Loci {
    private String Name;

    public Loci(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public abstract List<String> getParents();
    public abstract double evaluate(Map<String, Double> pas);
    public abstract double sample(Map<String, Double> pas);
    public abstract void fill(Gene gene);
    public abstract String getDefinition();
    public abstract JSONObject toJSON();
}
