package org.twz.cx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.*;

import org.twz.dataframe.DataFrame;
import org.twz.dataframe.TimeSeries;
import org.twz.dataframe.Tuple;
import org.twz.io.IO;
import org.twz.util.NameGenerator;

import java.util.*;
import java.util.stream.Collectors;


public abstract class DataModel extends BayesianModel {
    private NameGenerator NG;
    private ParameterModel SC;
    private Director Ctrl;
    private String SimModel, WarmUpModel;
    private final double Time0, Time1, Dt, TimeWarm;

    private TimeSeries LastOutput;
    private Parameters LastPars;
    private IY0 LastY0;
    private Map<String, List<Tuple<Parameters, IY0, TimeSeries>>> Mementos;

    public DataModel(Director ctrl, String bn, String simModel,
                     double t0, double t1, double dt,
                     String warmUpModel, double t_warm) {
        super(ctrl.getBayesNet(bn));
        NG = new NameGenerator("Sim");
        Ctrl = ctrl;
        SC = this.BN.toParameterModel(ctrl.getParameterHierarchy(simModel));
        SimModel = simModel;
        Time0 = t0;
        Time1 = t1;
        Dt = dt;
        TimeWarm = t_warm;
        WarmUpModel = warmUpModel;
        Mementos = new HashMap<>();
    }

    public DataModel(Director ctrl, String bn, String simModel,
                     double t0, double t1, double dt) {
        this(ctrl, bn, simModel, t0, t1, dt, null, 0);
    }


    @Override
    public Chromosome samplePrior() {
        return SC.generate(NG.getNext());
    }

    public IY0 warmUp(Chromosome pars) {
        IY0 y0 = sampleY0(pars);
        if (WarmUpModel == null) {
            return y0;
        }

        Parameters pc;
        if (pars instanceof Parameters) {
            pc = (Parameters) pars;
        } else {
            pc = new PseudoParameters(NG.getNext(), pars.getLocus());
        }

        AbsSimModel model = Ctrl.generateModel(pc.getName(), WarmUpModel, pc);
        try {
            Simulator.simulate(model, y0, 0, TimeWarm, 1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transportY0(model);
    }

    public TimeSeries simulate(Chromosome pars, IY0 y0) {
        Parameters pc;
        if (pars instanceof Parameters) {
            pc = (Parameters) pars;
        } else {
            pc = new PseudoParameters(NG.getNext(), pars.getLocus());
        }

        AbsSimModel model = Ctrl.generateModel(pc.getName(), SimModel, pc);
        try {
            Simulator.simulate(model, y0, Time0, Time1, Dt, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LastPars = pc;
        LastOutput = model.getObserver().getTimeSeries();
        LastY0 = y0;
        return LastOutput;
    }

    @Override
    public void evaluateLogLikelihood(Chromosome chromosome) {
        IY0 y0 = warmUp(chromosome);
        TimeSeries ts = null;
        if (!checkMidTerm(y0, chromosome)) {
            chromosome.setLogLikelihood(Double.NEGATIVE_INFINITY);
        } else {
            try {
                ts = simulate(chromosome, y0);
                chromosome.setLogLikelihood(calculateLogLikelihood(chromosome, ts));
            } catch (Exception e) {
                chromosome.setLogLikelihood(Double.NEGATIVE_INFINITY);
            }
        }
        if (ts != null) {
            LastPars = (Parameters) chromosome;
            LastOutput = ts;
            LastY0 = y0;
        }
    }

    @Override
    public void keepMemento(Chromosome chromosome, String type) {
        if (!Mementos.containsKey(type)) {
            Mementos.put(type, new ArrayList<>());
        }
        if (LastPars != chromosome) {
            evaluateLogLikelihood(chromosome);
        }
        Mementos.get(type).add(new Tuple<>(LastPars, LastY0, LastOutput));
    }

    @Override
    public void clearMementos(String type) {
        if (Mementos.containsKey(type)) Mementos.get(type).clear();
    }

    @Override
    public void clearMementos() {
        Mementos.clear();
    }

    public void saveMementosJSON(String file_path, String type) throws JSONException {
        JSONArray js = new JSONArray();

        JSONObject ent;
        List<Tuple<Parameters, IY0, TimeSeries>> sel = Mementos.get(type);

        for (Tuple<Parameters, IY0, TimeSeries> tup : sel) {
            ent = new JSONObject();
            ent.put("Name", tup.getFirst().getName());
            ent.put("Parameters", tup.getFirst().toJSON());
            ent.put("Y0", tup.getSecond().toJSON());
            ent.put("Simulation", tup.getThird().toJSON());
            js.put(ent);
        }
        IO.writeJSON(js, file_path);
    }

    public void saveMementosBySimulation(String path, String type, String prefix, String suffix) {
        List<Tuple<Parameters, IY0, TimeSeries>> sel = Mementos.get(type);
        NameGenerator ng = new NameGenerator(prefix);
        Map<String, TimeSeries> ts = new LinkedHashMap<>();
        Map<String, Parameters> pars = new LinkedHashMap<>();
        Map<String, IY0> y0s = new LinkedHashMap<>();

        if (sel.size() > 1) {
            for (Tuple<Parameters, IY0, TimeSeries> ent : sel) {
                String id = ng.getNext();
                pars.put(id, ent.getFirst());
                y0s.put(id, ent.getSecond());
                ts.put(id, ent.getThird());
            }
        } else {
            for (Tuple<Parameters, IY0, TimeSeries> ent : sel) {
                String id = "Simulation";
                pars.put(id, ent.getFirst());
                y0s.put(id, ent.getSecond());
                ts.put(id, ent.getThird());
            }
        }

        IO.checkDirectory(path);
        saveMementos(path, prefix, suffix, pars, y0s, ts);
    }

    public void saveMementosByVariable(String path, String type, String prefix, String suffix) {
        List<Tuple<Parameters, IY0, TimeSeries>> sel = Mementos.get(type);
        NameGenerator ng = new NameGenerator(prefix);
        Map<String, Parameters> pars = new LinkedHashMap<>();
        Map<String, IY0> y0s = new LinkedHashMap<>();
        Map<String, TimeSeries> ts = new LinkedHashMap<>();

        for (Tuple<Parameters, IY0, TimeSeries> ent : sel) {
            String id = ng.getNext();
            pars.put(id, ent.getFirst());
            y0s.put(id, ent.getSecond());
            ts.put(id, ent.getThird());
        }

        ts = TimeSeries.transpose(ts);
        IO.checkDirectory(path);
        saveMementos(path, prefix, suffix, pars, y0s, ts);
    }

    private void saveMementos(String path, String prefix, String suffix,
                              Map<String, Parameters> pars, Map<String, IY0> y0s, Map<String, TimeSeries> ts) {
        List<Map<String, Double>> p = pars.values().stream().map(Chromosome::toData).collect(Collectors.toList());
        if (suffix.endsWith(".csv")) {
            (new DataFrame(p)).toCSV(path+"/"+prefix+"Parameters"+suffix);

            for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                ent.getValue().toCSV(path+"/"+prefix+ent.getKey()+suffix);
            }
        } else if (suffix.endsWith(".json")) {
            try {
                (new DataFrame(p)).toJSON(path+"/"+prefix+"Parameters"+suffix);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject js = new JSONObject();
            for (Map.Entry<String, IY0> ent : y0s.entrySet()) {
                try {
                    js.put(ent.getKey(), ent.getValue().toJSON());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            IO.writeJSON(js, path+"/"+prefix+"Y0s"+suffix);
            try {
                for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                    ent.getValue().toJSON(path+"/"+prefix+ent.getKey()+suffix);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract IY0 sampleY0(Chromosome chromosome);

    protected abstract boolean checkMidTerm(IY0 y0, Chromosome chromosome);

    protected abstract IY0 transportY0(AbsSimModel model);

    protected abstract double calculateLogLikelihood(Chromosome chromosome, TimeSeries output);

    @Override
    public boolean hasExactLikelihood() {
        return false;
    }
}
