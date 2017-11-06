package pcore.loci;

import org.json.JSONObject;
import pcore.Gene;
import pcore.ScriptException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A loci with single value, usually a root
 * Created by TimeWz on 2017/4/16.
 */
public class ValueLoci extends Loci {
    private final static List<String> Parents = new ArrayList<>();

    private final double Value;

    public ValueLoci(String name, String val) throws ScriptException {
        super(name);
        double v;
        try {
            v = Double.parseDouble(val);
        } catch (Exception e) {
            throw new ScriptException(val);
        }
        Value = v;
    }

    public ValueLoci(String name, double val) {
        super(name);
        Value = val;
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
    public double sample(Map<String, Double> pas) {
        return Value;
    }

    @Override
    public void fill(Gene gene) {
        gene.put(getName(), Value);
    }

    @Override
    public String getDefinition() {
        return getName() + "= "+ Value;
    }

    @Override
    public String toString() {
        return getName() + ": "+ Value;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Type", "Value");
        js.put("Def", Value+"");
        return js;
    }
}
