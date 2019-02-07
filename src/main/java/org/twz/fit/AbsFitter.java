package org.twz.fit;

import org.twz.dag.BayesianModel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbsFitter {

    protected BayesianModel Model;
    protected Map<String, Double> Options;
    private Logger Log;

    public AbsFitter(BayesianModel model) {
        Model = model;
        Options = new HashMap<>();
        Log = null;
    }

    public void onLog(Logger log) {
        Log = log;
        Log.setLevel(Level.INFO);
    }

    public void onLog() {
        if (Log == null) {
            Log = Logger.getLogger(this.getClass().getSimpleName());
            Log.setLevel(Level.INFO);
        }
    }

    public void offLog() {
        Log = null;
    }

    protected void info(String msg) {
        Log.info(msg);
    }

    protected void warning(String msg) {
        Log.warning(msg);
    }

    protected void error(String msg) {
        Log.severe(msg);
    }

    public void setOptions(String key, double value) {
        Options.replace(key, value);
    }

    public abstract void fit(int niter);
}
