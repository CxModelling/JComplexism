package org.twz.dag.loci;

import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Gene;
import org.twz.io.AdapterJSONObject;

import java.util.Arrays;
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
    public abstract double evaluate(Gene gene);
    public abstract double sample(Map<String, Double> pas);
    public abstract double sample(Gene gene);
    public abstract void fill(Gene gene);
    public abstract String getDefinition();
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Name", this.Name);
        js.put("Def", this.getDefinition());
        return js;
    }

    public static List<String> parseParents(String fn) {
        Expression e = new Expression(fn);
        return Arrays.asList(e.getMissingUserDefinedArguments());
    }
}
