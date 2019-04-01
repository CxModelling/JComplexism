package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/4/16.
 */
public class FunctionLoci extends Loci {
    private final List<String> Parents, ParentFunctions;
    private final String Function;
    private final Expression E;

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
    public double evaluate(Chromosome chromosome) {
        return 0;
    }


    @Override
    public double render(Map<String, Double> pas) throws IncompleteConditionException {
        for (String e : Parents) {
            try {
                E.setArgumentValue(e, pas.get(e));
            } catch (NullPointerException ex) {
                throw new IncompleteConditionException(e);
            }
        }
        return E.calculate();
    }

    @Override
    public double render(Chromosome chromosome) throws IncompleteConditionException {
        for (String e : Parents) {
            try {
                E.setArgumentValue(e, chromosome.getDouble(e));
            } catch (NullPointerException ex) {
                throw new IncompleteConditionException(e);
            }
        }
        return E.calculate();
    }

    @Override
    public double render() throws IncompleteConditionException {
        return E.calculate();
    }

    @Override
    public void fill(Chromosome chromosome) throws IncompleteConditionException {
        chromosome.put(getName(), render(chromosome));
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
