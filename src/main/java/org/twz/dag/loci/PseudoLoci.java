package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Chromosome;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PseudoLoci extends Loci {
    private final List<String> Parents;
    private final String Function;

    public PseudoLoci(String name, String func) {
        super(name);
        Expression e = new Expression(func);
        Parents = Arrays.asList(e.getMissingUserDefinedArguments());
        Function = "f(" + Parents.stream().collect(Collectors.joining(","));

    }

    @Override
    public List<String> getParents() {
        return Parents;
    }

    @Override
    public double evaluate(Map<String, Double> pas) {
        return 0;
    }

    @Override
    public double evaluate(Chromosome chromosome) {
        return 0;
    }


    @Override
    public double sample(Map<String, Double> pas) {
        return Double.NaN;
    }

    @Override
    public double sample(Chromosome chromosome) {
        return Double.NaN;
    }

    @Override
    public void fill(Chromosome chromosome) {

    }

    @Override
    public String getDefinition() {
        return Function;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Type", "Pseudo");
        js.put("Parents", new JSONArray(getParents()));
        return js;
    }
}
