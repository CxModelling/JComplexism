package pcore.loci;

import org.json.JSONArray;
import org.json.JSONObject;
import pcore.Gene;
import pcore.ScriptException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by TimeWz on 2017/4/16.
 */
public class FunctionLoci extends Loci {
    private final static int Max = 100;

    private static ScriptEngine engine = (new ScriptEngineManager())
            .getEngineByName("JavaScript");


    private final List<String> Parents;
    private final String Function, ReformedFunction;

    public FunctionLoci(String name, String function) throws ScriptException {
        super(name);
        Function = function;
        ReformedFunction = reformFunction();
        Parents = parseParents(ReformedFunction);
    }

    public FunctionLoci(String name, String function, Collection<String> parents) {
        super(name);
        Function = function;
        ReformedFunction = reformFunction();
        Parents = new ArrayList<>(parents);
    }

    @Override
    public List<String> getParents() {
        return Parents;
    }


    private String reformFunction() {
        String fn = Function.replaceAll("\\s+", "");

        Set<String> fns = new HashSet<>();
        Pattern pat = Pattern.compile("(\\w+)\\(");
        Matcher mat = pat.matcher(Function);
        while(mat.find()) {
            fns.add(mat.group(1));
        }

        for (String s: fns) {
            fn = fn.replaceAll(s+"\\(", "Math."+s+"(");
        }

        return fn;
    }

    public static List<String> parseParents(String fn) throws ScriptException {
        List<String> parents = new ArrayList<>();
        String f = fn;
        int pas = 0;
        while (pas < Max) {
            try {
                engine.eval(f);
                break;
            } catch (javax.script.ScriptException e) {
                String mes = e.getMessage();
                mes = mes.split("\"")[1];
                if (parents.contains(mes)) {
                    throw new ScriptException(mes);
                }
                parents.add(mes);
                pas ++;
                f = f.replaceAll("\\b"+mes+"\\b", "0.87");
            }
        }

        if (pas == Max) {
            throw new ScriptException(fn);
        }

        return parents;
    }

    @Override
    public double evaluate(Map<String, Double> pas) {
        return 0;
    }

    @Override
    public double sample(Map<String, Double> pas) {
        String f = Function;
        for (String par: Parents) {
            f = f.replaceAll("\\b"+par+"\\b", pas.get(par).toString());
        }
        try {
            return (Double) engine.eval(f);
        } catch (javax.script.ScriptException e) {
            return 0;
        }

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
        JSONObject js = new JSONObject();
        js.put("Type", "Function");
        js.put("Def", Function);
        js.put("Parents", new JSONArray(getParents()));
        return js;
    }
}
