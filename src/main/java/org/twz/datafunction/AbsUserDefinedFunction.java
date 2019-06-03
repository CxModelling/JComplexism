package org.twz.datafunction;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.io.AdapterJSONObject;
import org.twz.io.FnJSON;
import org.twz.prob.IDistribution;

import java.util.HashMap;
import java.util.Map;


public abstract class AbsUserDefinedFunction extends AbsDataFunction {

    public AbsUserDefinedFunction(String name, int n_vars) {
        super(name, getDefaultVars(n_vars), null);
    }

    @Override
    public IDistribution getSampler(double[] values) {
        return null;
    }

    private static String[] getDefaultVars(int n) {
        String[] vs = new String[n];
        for (int i = 0; i < n; i++) {
            vs[i] = "Var" + i;
        }
        return vs;
    }
}
