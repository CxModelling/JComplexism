package org.twz.fit;

import org.twz.dag.BayesianModel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public abstract class AbsFitter {
    private class FittingFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%s]: %s\n", record.getLevel(), record.getMessage());
        }
    }


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
            Log.setUseParentHandlers(false);
            Log.setLevel(Level.INFO);
            Log.addHandler(new ConsoleHandler());
            //Log.addHandler(new StreamHandler(System.out, new FittingFormatter()));
        }
    }

    public void offLog() {
        Log = null;
    }

    protected void info(String msg) {
        if (Log != null) {
            Log.info(msg);
        }
    }

    protected void warning(String msg) {
        if (Log != null) Log.warning(msg);
    }

    protected void error(String msg) {
        if (Log != null) Log.severe(msg);
    }

    public void setOptions(String key, double value) {
        Options.replace(key, value);
    }

    public abstract void fit(int niter);
}
