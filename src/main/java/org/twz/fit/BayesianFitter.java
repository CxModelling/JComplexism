package org.twz.fit;

import org.json.JSONArray;
import org.json.JSONException;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;

import java.util.*;
import java.util.jar.JarException;

public abstract class BayesianFitter extends AbsFitter {

    private List<Gene> Prior;
    protected List<Gene> Posterior;

    public BayesianFitter(BayesianModel model) {
        super(model);

    }

    public List<Gene> getPosterior() {
        return Posterior;
    }

    public List<Gene> getPrior() {
        return Prior;
    }

    public void generatePrior(int niter) {
        Prior = new ArrayList<>();
        for (int i = 0; i < niter; i++) {
            Prior.add(Model.samplePrior());
        }
    }

    public JSONArray priorToJSON() throws JSONException {
        JSONArray js = new JSONArray();
        for (Gene p: Prior) {
            js.put(p.toJSON());
        }
        return js;
    }

    public JSONArray posteriorToJSON() throws JSONException {
        JSONArray js = new JSONArray();
        for (Gene p: Posterior) {
            js.put(p.toJSON());
        }
        return js;
    }

    public void summarisePrior() {
        (new Summarizer(Prior)).println();
    }

    public void summarisePosterior() {
        (new Summarizer(Posterior)).println();
    }

    public abstract Map<String, Double> getGoodnessOfFit();

}
