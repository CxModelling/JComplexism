package org.twz.cx;

import org.json.JSONException;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.*;

import org.twz.dataframe.DataFrame;
import org.twz.dataframe.TimeSeries;
import org.twz.io.IO;
import org.twz.util.NameGenerator;

import java.util.*;


public abstract class DataModel extends BayesianModel {
    private NameGenerator NG;
    private SimulationCore SC;
    private Director Ctrl;
    private String SimModel, WarmUpModel;
    private final double Time0, Time1, Dt, TimeWarm;

    private TimeSeries LastOutput;
    private ParameterCore LastPars;
    private Map<String, Map<ParameterCore, TimeSeries>> Mementos;

    public DataModel(Director ctrl, String bn, String simModel,
                     double t0, double t1, double dt,
                     String warmUpModel, double t_warm) {
        super(ctrl.getBayesNet(bn));
        NG = new NameGenerator("Sim");
        Ctrl = ctrl;
        SC = this.BN.toSimulationCore(ctrl.getParameterHierarchy(simModel), true);
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
    public Gene samplePrior() {
        return SC.generate(NG.getNext());
    }

    public IY0 warmUp(Gene pars) {
        IY0 y0 = sampleY0(pars);
        if (WarmUpModel == null) {
            return y0;
        }

        ParameterCore pc;
        if (pars instanceof ParameterCore) {
            pc = (ParameterCore) pars;
        } else {
            pc = new PseudoParameterCore(NG.getNext(), pars.getLocus());
        }

        AbsSimModel model = Ctrl.generateModel(pc.getName(), WarmUpModel, pc);
        try {
            Simulator.simulate(model, y0, 0, TimeWarm, 1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transportY0(model);
    }

    public TimeSeries simulate(Gene pars, IY0 y0) {
        ParameterCore pc;
        if (pars instanceof ParameterCore) {
            pc = (ParameterCore) pars;
        } else {
            pc = new PseudoParameterCore(NG.getNext(), pars.getLocus());
        }

        AbsSimModel model = Ctrl.generateModel(pc.getName(), SimModel, pc);
        try {
            Simulator.simulate(model, y0, Time0, Time1, Dt, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LastPars = pc;
        LastOutput = model.getObserver().getTimeSeries();
        return LastOutput;
    }

    @Override
    public void evaluateLogLikelihood(Gene gene) {
        IY0 y0 = warmUp(gene);
        TimeSeries ts = null;
        if (!checkMidTerm(y0, gene)) {
            gene.setLogLikelihood(Double.NEGATIVE_INFINITY);
        } else {
            try {
                ts = simulate(gene, y0);
                gene.setLogLikelihood(calculateLogLikelihood(gene, ts));
            } catch (Exception e) {
                gene.setLogLikelihood(Double.NEGATIVE_INFINITY);
            }
        }
        if (ts != null) {
            LastPars = (ParameterCore) gene;
            LastOutput = ts;
        }
    }

    @Override
    public void keepMemento(Gene gene, String type) {
        if (!Mementos.containsKey(type)) {
            Mementos.put(type, new LinkedHashMap<>());
        }
        if (LastPars != gene) {
            evaluateLogLikelihood(gene);
        }
        Mementos.get(type).put(LastPars, LastOutput);
    }

    @Override
    public void clearMementos(String type) {
        if (Mementos.containsKey(type)) Mementos.get(type).clear();
    }

    @Override
    public void clearMementos() {
        Mementos.clear();
    }

    public void saveMementosBySimulation(String path, String type, String prefix, String suffix) {
        Map<ParameterCore, TimeSeries> sel = Mementos.get(type);
        NameGenerator ng = new NameGenerator(prefix);
        Map<String, TimeSeries> ts = new LinkedHashMap<>();
        List<Map<String, Double>> pars = new ArrayList<>();

        if (sel.size() > 1) {
            for (Map.Entry<ParameterCore, TimeSeries> ent : sel.entrySet()) {
                String id = ng.getNext();
                pars.add(ent.getKey().getLocus());
                ts.put(id, ent.getValue());
            }
        } else {
            for (Map.Entry<ParameterCore, TimeSeries> ent : sel.entrySet()) {
                String id = "Simulation";
                pars.add(ent.getKey().getLocus());
                ts.put(id, ent.getValue());
            }
        }

        IO.checkDirectory(path);
        saveMementos(path, prefix, suffix, ts, pars);
    }

    public void saveMementosByVariable(String path, String type, String prefix, String suffix) {
        Map<ParameterCore, TimeSeries> sel = Mementos.get(type);
        NameGenerator ng = new NameGenerator(prefix);
        Map<String, TimeSeries> ts = new LinkedHashMap<>();
        List<Map<String, Double>> pars = new ArrayList<>();

        for (Map.Entry<ParameterCore, TimeSeries> ent : sel.entrySet()) {
            String id = ng.getNext();
            pars.add(ent.getKey().getLocus());
            ts.put(id, ent.getValue());
        }

        ts = TimeSeries.transpose(ts);
        IO.checkDirectory(path);
        saveMementos(path, prefix, suffix, ts, pars);
    }

    private void saveMementos(String path, String prefix, String suffix, Map<String, TimeSeries> ts, List<Map<String, Double>> pars) {
        if (suffix.endsWith(".csv")) {
            (new DataFrame(pars)).toCSV(path+"/"+prefix+"Parameters"+suffix);
            for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                ent.getValue().toCSV(path+"/"+prefix+ent.getKey()+suffix);
            }
        } else if (suffix.endsWith(".json")) {
            try {
                (new DataFrame(pars)).toJSON(path+"/"+prefix+"Parameters"+suffix);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                for (Map.Entry<String, TimeSeries> ent : ts.entrySet()) {
                    ent.getValue().toJSON(path+"/"+prefix+ent.getKey()+suffix);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract IY0 sampleY0(Gene gene);

    protected abstract boolean checkMidTerm(IY0 y0, Gene gene);

    protected abstract IY0 transportY0(AbsSimModel model);

    protected abstract double calculateLogLikelihood(Gene gene, TimeSeries output);

    @Override
    public boolean hasExactLikelihood() {
        return false;
    }
}
