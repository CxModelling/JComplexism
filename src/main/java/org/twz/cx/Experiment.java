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
import org.twz.dataframe.Pair;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.Tuple;
import org.twz.exception.ValidationException;
import org.twz.io.IO;
import org.twz.util.NameGenerator;

import java.util.*;

public abstract class Experiment {

    private List<Tuple<String, Parameters, IY0>> Inputs;

    private ParameterModel SC;
    protected Director Ctrl;
    private String SimModel;
    private double Time0, Time1, DiffTime;
    private Map<String, TimeSeries> Mementos;

    public Experiment(Director ctrl, String bn, String simModel, double t0, double t1, double dt) throws ValidationException {

        Ctrl = ctrl;
        SC = ctrl.getBayesNet(bn).toParameterModel(ctrl.getParameterHierarchy(simModel));
        SimModel = simModel;
        Time0 = t0;
        Time1 = t1;
        DiffTime = dt;
        Mementos = new LinkedHashMap<>();
    }

    public void loadPosterior(JSONArray js) throws JSONException {
        Inputs = new ArrayList<>();

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
            Inputs.add(new Tuple<>(name, pars, y0));
        }
    }


    protected abstract IY0 translateY0(JSONObject js);


    public Pair<Chromosome, TimeSeries> testRun(String log) throws ValidationException {
        Parameters pars = Inputs.get(0).getSecond();
        IY0 y0 = Inputs.get(0).getThird();

        AbsSimModel model = Ctrl.generateModel(pars.getName(), SimModel, pars);
        Simulator simulator = new Simulator(model);

        if (log != null) {
            simulator.onLog(log);
        } else {
            simulator.offLog();
        }

        try {
            simulator.simulate(y0, Time0, Time1, DiffTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(pars, model.outputTS());
    }

    public Pair<Chromosome, TimeSeries> testRun() throws ValidationException {
        return testRun(null);
    }

    public void start(int max_iter) {

        Mementos.clear();
        Parameters pc;
        IY0 y0;

        NameGenerator ng = new NameGenerator("Sim");
        String name;
        AbsSimModel model;
        TimeSeries ts;


        while (true) {
            for (Tuple<String, Parameters, IY0> ent : Inputs) {
                pc = ent.getSecond();
                y0 = ent.getThird();

                name = ng.getNext();

                try {
                    model = Ctrl.generateModel(name, SimModel, pc);
                    ts = Simulator.simulate(model, y0, Time0, Time1, DiffTime, true);
                    Mementos.put(name, ts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Mementos.size() >= max_iter) {
                    return;
                }
            }
        }
    }

    public void run(String path, String prefix, String suffix) {
        run(0, Inputs.size(), path, prefix, suffix);
    }

    public void run(int to, String path, String prefix, String suffix) {
        run(0, to, path, prefix, suffix);
    }

    public void run(int from, int to, String path, String prefix, String suffix) {
        to = Math.max(Math.min(Inputs.size(), to), from + 1);

        Parameters pars;
        IY0 y0;

        IO.checkDirectory(path);

        AbsSimModel model;
        TimeSeries ts;


        for (int i = from; i < to; i++) {
            pars = Inputs.get(i).getSecond();
            y0 = Inputs.get(i).getThird();

            try {
                model = Ctrl.generateModel(Inputs.get(i).getFirst(), SimModel, pars);
                ts = Simulator.simulate(model, y0, Time0, Time1, DiffTime, true);
                saveResults(path, prefix, String.format("%06d", i), suffix, ts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveResultsByVariable(String file_path, String prefix, String suffix) {
        Map<String, TimeSeries> ts = TimeSeries.transpose(Mementos);
        IO.checkDirectory(file_path);
        saveResults(file_path, prefix, suffix, ts);
    }

    private void saveResults(String path, String prefix, String suffix, Map<String, TimeSeries> ts) {
        String file_path;
        if (suffix.endsWith(".csv")) {
            for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                file_path = prefix+ent.getKey()+suffix;
                file_path = file_path.replaceAll(":", "_");
                file_path = path + "/" + file_path;
                ent.getValue().toCSV(file_path);
            }
        } else if (suffix.endsWith(".json")) {
            try {
                for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                    file_path = prefix+ent.getKey()+suffix;
                    file_path = file_path.replaceAll(":", "_");
                    file_path = path + "/" + file_path;
                    ent.getValue().toJSON(file_path);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveResults(String path, String prefix, String name, String suffix, TimeSeries ts) {
        String file_path;

        file_path = prefix+name+suffix;
        file_path = file_path.replaceAll(":", "_");
        file_path = path + "/" + file_path;

        if (suffix.endsWith(".csv")) {
            ts.toCSV(file_path);
        } else if (suffix.endsWith(".json")) {
            try {
            ts.toJSON(file_path);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
