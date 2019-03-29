package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dag.Gene;
import java.util.*;


/**
 *
 * Created by TimeWz on 2017/4/16.
 */
public class FunctionLoci extends Loci {
    private final List<String> Parents, ParentFunctions;
    private final String Function;
    public final Expression E;

    public FunctionLoci(String name, String function) {
        super(name);
        Function = function;
        E = new Expression(function);
        Parents = Arrays.asList(E.getMissingUserDefinedArguments());
        Parents.forEach(e->E.defineArgument(e, Double.NaN));
        ParentFunctions = Arrays.asList(E.getMissingUserDefinedFunctions());
    }

    public FunctionLoci(String name, String function, Collection<String> parents) {
        super(name);
        Function = function;
        E = new Expression(function);
        Parents = new ArrayList<>(parents);
        Parents.forEach(e->E.defineArgument(e, Double.NaN));
        ParentFunctions = Arrays.asList(E.getMissingUserDefinedFunctions());
    }

    @Override
    public List<String> getParents() {
        return Parents;
    }

    public boolean needsFunction(String fn) {
        return ParentFunctions.contains(fn);
    }

    public void linkToParentFunction(String fn, FunctionExtension fe) {
        E.addFunctions(new Function(fn, fe));
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
        Parents.forEach(e->E.setArgumentValue(e, gene.getDouble(e)));
        return E.calculate();
    }

    @Override
    public void fill(Gene gene) {
        gene.put(getName(), sample(gene));
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
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Type", "Function");
        js.put("Parents", new JSONArray(getParents()));
        return js;
    }
}
