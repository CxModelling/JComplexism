package org.twz.dag.loci;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Chromosome;
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
    public abstract double evaluate(Chromosome chromosome);
    public abstract double sample(Map<String, Double> pas);
    public abstract double sample(Chromosome chromosome);
    public abstract void fill(Chromosome chromosome);
    public abstract String getDefinition();
    public JSONObject toJSON() throws JSONException {
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
