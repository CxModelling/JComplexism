package org.twz.cx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.abmodel.statespace.StSpABMBlueprint;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.ODEEBMBlueprint;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Chromosome;
import org.twz.dataframe.Pair;
import org.twz.dataframe.TimeSeries;
import org.twz.fit.BayesianFitter;
import org.twz.fit.MCMC;
import org.twz.io.FnJSON;
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;

public class ModellingFlowTest {

    private Director Ctrl;
    private DataModelSIR DM_EBM, DM_Prior;

    @Before
    public void setUp() throws Exception {
        Ctrl = new Director();
        Ctrl.loadBayesNet("src/test/resources/SIR/pSIR_prior.txt");
        Ctrl.loadBayesNet("src/test/resources/SIR/pSIR_ebm.txt");
        Ctrl.loadBayesNet("src/test/resources/SIR/pSIR_abm.txt");
        Ctrl.loadBayesNet("src/test/resources/SIR/pSIR_hy.txt");
        Ctrl.loadBayesNet("src/test/resources/SIR/dm.txt");

        Ctrl.loadStateSpace("src/test/resources/SIR/dSIR_abm.txt");
        Ctrl.loadStateSpace("src/test/resources/SIR/dSIR_hy.txt");

        // setting prior
        ODEEBMBlueprint bp = (ODEEBMBlueprint) Ctrl.createSimModel("Prior", "ODEEBM");

        bp.setODE((t, y0, y1, ps, x) -> {
            double beta = ps.getDouble("beta"),
                    gamma = ps.getDouble("gamma"), delta = ps.getDouble("delta");
            double n = y0[0] + y0[1] + y0[2];
            double foi = beta * y0[0] * y0[1] / n;
            y1[0] = - foi + delta*(y0[1] + y0[2]);
            y1[1] = foi - (gamma+delta) * y0[1];
            y1[2] = gamma * y0[1] - delta * y0[2];
        }, new String[]{"Sus", "Inf", "Rec"});

        bp.addMeasurementFunction((tab, ti, ys, ps, x) -> {
            double n = ys[0] + ys[1] + ys[2];
            double inc = ps.getDouble("beta") * ys[0] * ys[1] / n / n;
            tab.put("inc_rate", inc);
            tab.put("n", n);
        });
        bp.setRequiredParameters(new String[]{"beta", "gamma", "delta"});

        // setting ebm
        bp = (ODEEBMBlueprint) Ctrl.createSimModel("EBM", "ODEEBM");

        bp.setODE((t, y0, y1, ps, attributes) -> {
            double beta = ps.getDouble("beta"),
                    gamma = ps.getDouble("gamma"), delta = ps.getDouble("delta");
            double n = y0[0] + y0[1] + y0[2];
            double foi = beta * y0[0] * y0[1] / n;
            y1[0] = - foi + delta*(y0[1] + y0[2]);
            y1[1] = foi - (gamma+delta) * y0[1];
            y1[2] = gamma * y0[1] - delta * y0[2];
        }, new String[]{"Sus", "Inf", "Rec"});

        bp.addMeasurementFunction((tab, ti, ys, parameters, x) -> {
            double n = ys[0] + ys[1] + ys[2];
            tab.put("Prv", ys[1]/n);
            double beta = parameters.getDouble("beta");

            tab.put("Inc", beta * ys[0] * ys[1] / n /n);
            tab.put("n", n);
        });
        bp.setRequiredParameters(new String[]{"beta", "gamma", "delta"});
        bp.setObservations(new String[]{"Sus", "Inf", "Rec"});

        DM_Prior = new DataModelSIR(Ctrl, "pSIR_prior", "Prior", 0,  10, 1, "Prior", 100);
        DM_EBM = new DataModelSIR(Ctrl, "pSIR_ebm", "EBM", 0,  10, 1, "Prior", 100);
        // setting abm

        StSpABMBlueprint bpa = (StSpABMBlueprint) Ctrl.createSimModel("ABM", "StSpABM");
        bpa.setAgent("Ag", "agent", "SIR_abm");

        bpa.addBehaviour("{'Name': 'FOI', 'Type': 'FDShock', 'Args': {'s_src': 'Inf', 't_tar': 'Infect'}}");
        bpa.addBehaviour("{'Name': 'Life', 'Type': 'Reincarnation', 'Args': {'s_death': 'Dead', 's_birth': 'Sus'}}");
        bpa.setObservations(new String[]{"Sus", "Inf", "Rec"}, new String[]{"Infect"}, new String[]{"FOI", "Life"});

        // Load data

        TimeSeries data = TimeSeries.readCSV("src/test/resources/SIR/ToFit.csv", "Time");
        DM_Prior.setData(data, "DataModel");
        // DataModelSIR.setUpModel(Ctrl);

        // DM = new DataModelSIR(Ctrl, "pCloseSIR", "SIR", 0, 7, 1);
        // DM.setData(data, "DataSIR");
    }

    @Test
    public void simulate() throws Exception {
        Pair<Chromosome, TimeSeries> p_ts = DM_EBM.testRun();
        p_ts.getSecond().print();
        //p_ts.getSecond().toCSV("src/test/resources/SIR/ToFit.csv");
        System.out.println(p_ts.getFirst().toString());
    }

    @Test
    public void fit() throws Exception {
        BayesianFitter fitter = new MCMC(1000, 1000, 3, DM_Prior.getMovableNodes());
        DM_Prior.fit(fitter);
        fitter.summariseParameters(DM_Prior.getResults());
        DM_Prior.saveMementosJSON("src/test/resources/SIR/Fitted.json", "Posterior");
    }

    @Test
    public void replicateEBM() throws Exception {
        Experiment exp = new Experiment(Ctrl, "pSIR_ebm", "EBM", 0, 10, 1) {
            @Override
            protected IY0 translateY0(JSONObject js) {
                EBMY0 y0 = new EBMY0();
                try {
                    JSONArray entries = js.getJSONArray("Entries");
                    for (int i = 0; i < entries.length(); i++) {
                        y0.append(entries.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return y0;
            }
        };
        exp.loadPosterior(IO.loadJSONArray("src/test/resources/SIR/Fitted.json"));
        Pair<Chromosome, TimeSeries> p_ts = exp.testRun();
        p_ts.getSecond().print();
        // exp.start(2000);
        // exp.saveResultsByVariable("E://test", "ebm_", ".csv");
    }

    @Test
    public void replicateABM() throws Exception {
        Experiment exp = new Experiment(Ctrl, "pSIR_abm", "ABM", 0, 10, 1) {
            @Override
            protected IY0 translateY0(JSONObject js) {
                StSpY0 y0 = new StSpY0();
                try {
                    JSONArray entries = js.getJSONArray("Entries");
                    JSONObject ent;
                    for (int i = 0; i < entries.length(); i++) {
                        ent = entries.getJSONObject(i);
                        y0.append(ent.getString("y"), ent.getInt("n"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return y0;
            }
        };
        exp.loadPosterior(IO.loadJSONArray("src/test/resources/SIR/Fitted.json"));
        Pair<Chromosome, TimeSeries> p_ts = exp.testRun();
        p_ts.getSecond().print();
        // exp.start(5);
        // exp.saveResultsByVariable("E://test", "abm_", ".csv");
    }

    @Test
    public void replicateHybrid() throws Exception {

    }
}