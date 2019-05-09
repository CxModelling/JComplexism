package org.twz.cx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.ParameterModel;
import org.twz.dag.Parameters;
import org.twz.dataframe.Pair;
import org.twz.dataframe.TimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Experiment {

    private Map<String, Pair<Parameters, IY0>> Inputs;

    private ParameterModel SC;
    protected Director Ctrl;
    private String SimModel;
    private double Time0, Time1, DiffTime;
    private Map<String, TimeSeries> Mementos;

    public Experiment(Director ctrl, String bn, String simModel,
                     double t0, double t1, double dt) {

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
        String name;
        Parameters pars;
        IY0 y0;
        for (int i = 0; i < js.length(); i++) {
            obj = js.getJSONObject(i);
            name = obj.getString("Name");
            pars = SC.generate(obj.getJSONObject("Parameters"));
            y0 = translateY0(obj.getJSONObject("Y0s"));
            Inputs.put(name, new Pair<>(pars, y0));
        }
    }


    protected abstract IY0 translateY0(JSONObject js);


    public TimeSeries singleRun(String log) {
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

        return model.outputTS();
    }

    public TimeSeries singleRun() {
        return singleRun(null);
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

            if (amp > 1) {
                model = Ctrl.generateModel(pc.getName(), SimModel, pc);
                try {
                    ts = Simulator.simulate(model, y0, Time0, Time1, DiffTime, false);
                    Mementos.put(ent.getKey(), ts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                for (int i = 1; i <= amp; i++) {
                    model = Ctrl.generateModel(pc.getName(), SimModel, pc);
                    try {
                        ts = Simulator.simulate(model, y0, Time0, Time1, DiffTime, false);
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

    public void save(String file_path) {

    }
}
