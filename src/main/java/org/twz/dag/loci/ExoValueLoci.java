package org.twz.dag.loci;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;

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
    public double evaluate(Chromosome chromosome) {
        return 0;
    }

    @Override
    public double render(Map<String, Double> pas) {
        return 0;
    }

    @Override
    public double render(Chromosome chromosome) {
        return 0;
    }

    @Override
    public double render() {
        return 0;
    }

    @Override
    public void fill(Chromosome chromosome) {

    }

    @Override
    public String getDefinition() {
        return this.getName();
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Type", "ExoValue");
        return js;
    }
}
