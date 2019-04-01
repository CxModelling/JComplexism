package org.twz.fit;

import org.json.JSONArray;
import org.json.JSONException;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;
import org.twz.dataframe.DataFrame;

import java.util.*;

public abstract class BayesianFitter extends AbsFitter {

    public BayesianFitter() {
        super();
        Options.put("N_post", 1000);
        Options.put("N_update", 200);
    }

    public void update(BayesianModel bm, int niter) {
        setOption("N_post", niter);
    }

    public JSONArray parametersToJSON(List<Chromosome> pars) throws JSONException {
        JSONArray js = new JSONArray();
        for (Chromosome p: pars) {
            js.put(p.toJSON());
        }
        return js;
    }

    public DataFrame summariseParameters(List<Chromosome> pars) {
        (new Summarizer(pars)).println();
        return null; // todo
    }

}
