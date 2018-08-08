package org.twz.dag.loci;

import org.json.JSONObject;
import org.twz.dag.Gene;

import java.util.List;
import java.util.Map;

public class ExoValueLoci extends Loci {


    public ExoValueLoci(String name) {
        super(name);
    }

    @Override
    public List<String> getParents() {
        return null;
    }

    @Override
    public double evaluate(Map<String, Double> pas) {
        return 0;
    }

    @Override
    public double sample(Map<String, Double> pas) {
        return 0;
    }

    @Override
    public double sample(Gene gene) {
        return 0;
    }

    @Override
    public void fill(Gene gene) {

    }

    @Override
    public String getDefinition() {
        return this.getName();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = super.toJSON();
        js.put("Type", "ExoValue");
        return js;
    }
}
