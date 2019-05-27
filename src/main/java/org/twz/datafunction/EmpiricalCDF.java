package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.graph.DiGraph;
import org.twz.io.FnJSON;
import org.twz.prob.Empirical;
import org.twz.prob.IDistribution;


public class EmpiricalCDF extends AbsDataFunction {

    private Empirical Dist;

    public EmpiricalCDF(String name, JSONObject df) throws JSONException {
        super(name, new String[]{}, df);
        Dist = new Empirical(name,
                    FnJSON.toDoubleArray(df.getJSONArray("Ps")),
                    FnJSON.toDoubleArray(df.getJSONArray("Xs")
                ));
    }

    @Override
    public IDistribution getSampler(double[] values) {
        return Dist;
    }

    @Override
    public double calculate() {
        return 0;
    }
}
