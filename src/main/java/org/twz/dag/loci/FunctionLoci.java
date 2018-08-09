package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Gene;
import java.util.*;


/**
 *
 * Created by TimeWz on 2017/4/16.
 */
public class FunctionLoci extends Loci {
    private final List<String> Parents;
    private final String Function;
    private final Expression E;

    public FunctionLoci(String name, String function) {
        super(name);
        Function = function;
        E = new Expression(function);
        Parents = Arrays.asList(E.getMissingUserDefinedArguments());
        Parents.forEach(e->E.defineArgument(e, Double.NaN));
    }

    public FunctionLoci(String name, String function, Collection<String> parents) {
        super(name);
        Function = function;
        E = new Expression(function);
        Parents = new ArrayList<>(parents);
        Parents.forEach(e->E.defineArgument(e, Double.NaN));
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
    public double evaluate(Gene gene) {
        return 0;
    }


    @Override
    public double sample(Map<String, Double> pas) {
        Parents.forEach(e->E.setArgumentValue(e, pas.get(e)));
        return E.calculate();
    }

    @Override
    public double sample(Gene gene) {
        Parents.forEach(e->E.setArgumentValue(e, gene.get(e)));
        return E.calculate();
    }

    @Override
    public void fill(Gene gene) {
        gene.put(getName(), sample(gene.getLocus()));
    }

    @Override
    public String getDefinition() {
        return getName() +"="+ Function;
    }

    @Override
    public String toString() {
        return getName() +": "+ Function;
    }


    @Override
    public JSONObject toJSON() {
        JSONObject js = super.toJSON();
        js.put("Type", "Function");
        js.put("Parents", new JSONArray(getParents()));
        return js;
    }
}
