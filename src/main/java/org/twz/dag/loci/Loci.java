package org.twz.dag.loci;

import org.json.JSONObject;
import org.twz.dag.Gene;
import org.twz.io.AdapterJSONObject;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/4/16.
 */
public abstract class Loci implements AdapterJSONObject {
    private String Name;

    Loci(String name) {
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
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Name", this.Name);
        js.put("Def", this.getDefinition());
        return js;
    }
}
