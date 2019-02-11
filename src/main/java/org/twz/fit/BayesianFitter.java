package org.twz.fit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Gene;
import org.twz.dataframe.DataFrame;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.jar.JarException;

public abstract class BayesianFitter extends AbsFitter {

    public BayesianFitter() {
        super();
        Options.put("N_post", 1000);
        Options.put("N_update", 200);
    }

    public void update(BayesianModel bm, int niter) {
        setOption("N_post", niter);
    }

    public JSONArray parametersToJSON(List<Gene> pars) throws JSONException {
        JSONArray js = new JSONArray();
        for (Gene p: pars) {
            js.put(p.toJSON());
        }
        return js;
    }

    public DataFrame summariseParameters(List<Gene> pars) {
        (new Summarizer(pars)).println();
        return null; // todo
    }

    void appendPriorUntil(BayesianModel bm, int n, List<Gene> prior) {
        while(prior.size() < n) {
            Gene gene = bm.samplePrior();
            if (!gene.isPriorEvaluated()) bm.evaluateLogPrior(gene);
            if (!gene.isLikelihoodEvaluated()) bm.evaluateLogLikelihood(gene);
            if (Double.isInfinite(gene.getLogLikelihood())) continue;
            prior.add(gene);
        }
    }
}
