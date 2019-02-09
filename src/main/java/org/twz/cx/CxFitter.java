package org.twz.cx;

import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.*;
import org.twz.dataframe.TimeSeries;
import org.twz.misc.NameGenerator;


public abstract class CxFitter extends BayesianModel {
    private NameGenerator NG;
    private SimulationCore SC;
    private Director Ctrl;
    private String SimModel, WarmUpModel;
    private final double Time0, Time1, Dt, TimeWarm;

    public CxFitter(Director ctrl, String bn, String simModel,
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
    }

    public CxFitter(Director ctrl, String bn, String simModel,
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
        return model.getObserver().getTimeSeries();
    }

    @Override
    public void evaluateLogLikelihood(Gene gene) {
        IY0 y0 = warmUp(gene);
        if (!checkMidTerm(y0, gene)) {
            gene.setLogLikelihood(Double.NEGATIVE_INFINITY);
        } else {
            try {
                TimeSeries ts = simulate(gene, y0);
                gene.setLogLikelihood(calculateLogLikelihood(gene, ts));
            } catch (Exception e) {
                gene.setLogLikelihood(Double.NEGATIVE_INFINITY);
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
