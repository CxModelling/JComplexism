package org.twz.dag.loci;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;
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
    public abstract double evaluate(Map<String, Double> pas) throws IncompleteConditionException;
    public abstract double evaluate(Chromosome chromosome) throws IncompleteConditionException;
    public abstract double render(Map<String, Double> pas) throws IncompleteConditionException;
    public abstract double render(Chromosome chromosome) throws IncompleteConditionException;
    public abstract double render() throws IncompleteConditionException;
    public abstract void fill(Chromosome chromosome) throws IncompleteConditionException;


    public abstract String getDefinition();

    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", this.Name);
        js.put("Def", this.getDefinition());
        return js;
    }

    protected static List<String> parseParents(String fn) {
        Expression e = new Expression(fn);
        return Arrays.asList(e.getMissingUserDefinedArguments());
    }
}
