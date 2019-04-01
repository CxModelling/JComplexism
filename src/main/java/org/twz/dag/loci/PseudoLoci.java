package org.twz.dag.loci;

import org.json.*;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Chromosome;

import java.util.*;



public class PseudoLoci extends Loci {
    private final List<String> Parents;
    private final String Function;

    public PseudoLoci(String name, String func) {
        super(name);
        Expression e = new Expression(func);
        Parents = Arrays.asList(e.getMissingUserDefinedArguments());
        Function = "f(" + String.join(",", Parents);

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
    public double render(Map<String, Double> pas) {
        return Double.NaN;
    }

    @Override
    public double render(Chromosome chromosome) {
        return Double.NaN;
    }

    @Override
    public double render() {
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
