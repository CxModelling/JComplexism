package org.twz.fit;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.BayesianModel;

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

    OutputSummary getSummary(BayesianModel bm, boolean seq) {
        return new OutputSummary(bm, seq);
    }



    @Override
    public JSONObject getGoodnessOfFit(BayesianModel bm) {
        try {
            return getSummary(bm).outputGoodnessOfFit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
