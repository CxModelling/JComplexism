package org.twz.cx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.Chromosome;
import org.twz.dag.ParameterModel;
import org.twz.dag.Parameters;
import org.twz.dataframe.DataFrame;
import org.twz.dataframe.Pair;
import org.twz.dataframe.TimeSeries;
import org.twz.io.IO;
import org.twz.util.NameGenerator;

import java.util.*;

public abstract class Experiment {

    private Map<String, Pair<Parameters, IY0>> Inputs;

    private ParameterModel SC;
    protected Director Ctrl;
    private String SimModel;
    private double Time0, Time1, DiffTime;
    private Map<String, TimeSeries> Mementos;

    public Experiment(Director ctrl, String bn, String simModel, double t0, double t1, double dt) {

        Ctrl = ctrl;
        SC = ctrl.getBayesNet(bn).toParameterModel(ctrl.getParameterHierarchy(simModel));
        SimModel = simModel;
        Time0 = t0;
        Time1 = t1;
        DiffTime = dt;
        Mementos = new HashMap<>();
    }

    public void loadPosterior(JSONArray js) throws JSONException {
        Inputs = new LinkedHashMap<>();

        JSONObject obj;
        NameGenerator NG = new NameGenerator("Sim");
        String name;
        Parameters pars;
        IY0 y0;
        for (int i = 0; i < js.length(); i++) {
            obj = js.getJSONObject(i);
            name = NG.getNext();
            pars = SC.generate(obj.getJSONObject("Parameters"));
            y0 = translateY0(obj.getJSONObject("Y0"));
            Inputs.put(name, new Pair<>(pars, y0));
        }
    }


    protected abstract IY0 translateY0(JSONObject js);


    public Pair<Chromosome, TimeSeries> testRun(String log) {
        String key = (new ArrayList<>(Inputs.keySet()).get(0));
        Parameters pc = Inputs.get(key).getFirst();
        IY0 y0 = Inputs.get(key).getSecond();

        AbsSimModel model = Ctrl.generateModel(pc.getName(), SimModel, pc);
        Simulator simulator = new Simulator(model);

        if (log != null) {
            simulator.onLog(log);
        }

        try {
            simulator.simulate(y0, Time0, Time1, DiffTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(pc, model.outputTS());
    }

    public Pair<Chromosome, TimeSeries> testRun() {
        return testRun(null);
    }

    public void start(int amp) {

        Mementos.clear();
        Parameters pc;
        IY0 y0;

        AbsSimModel model;
        TimeSeries ts;
        for (Map.Entry<String, Pair<Parameters, IY0>> ent : Inputs.entrySet()) {
            pc = ent.getValue().getFirst();
            y0 = ent.getValue().getSecond();

            if (amp == 1) {
                model = Ctrl.generateModel(pc.getName(), SimModel, pc);
                try {
                    ts = Simulator.simulate(model, y0, Time0, Time1, DiffTime, true);
                    Mementos.put(ent.getKey(), ts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                for (int i = 1; i <= amp; i++) {
                    model = Ctrl.generateModel(pc.getName(), SimModel, pc);
                    try {
                        ts = Simulator.simulate(model, y0, Time0, Time1, DiffTime, true);
                        Mementos.put(ent.getKey() + ":" + i, ts);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    public void start() {
        start(1);
    }

    public void saveResultsByVariable(String file_path, String prefix, String suffix) {
            Map<String, TimeSeries> ts = TimeSeries.transpose(Mementos);
            IO.checkDirectory(file_path);
        saveResults(file_path, prefix, suffix, ts);
    }

    private void saveResults(String path, String prefix, String suffix, Map<String, TimeSeries> ts) {

        if (suffix.endsWith(".csv")) {
            for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                ent.getValue().toCSV(path+"/"+prefix+ent.getKey()+suffix);
            }
        } else if (suffix.endsWith(".json")) {
            try {
                for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                    ent.getValue().toJSON(path+"/"+prefix+ent.getKey()+suffix);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
