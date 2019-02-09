package org.twz.cx.mcore;

import org.json.JSONException;
import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;
import org.twz.dataframe.TimeSeries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class Simulator {
    private class SimFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%s]: %s\n", record.getLevel(), record.getMessage());
        }
    }

    private AbsSimModel Model;
    private double Time;
    private boolean Recording, Logging;

    private Logger Log;
    private Map<String, AbsSimModel> LazyModels;

    public Simulator(AbsSimModel model, boolean rec) {
        Model = model;
        Time = 0;
        Recording = rec;
        Logging = false;
        Log = Logger.getLogger(model.getName());
        Log.setUseParentHandlers(false);
        LazyModels = new HashMap<>();
    }

    public Simulator(AbsSimModel model) {
        this(model, true);
    }

    public void onLog() {
        Logging = true;
    }

    public void onLog(String pat) {
        onLog();
        addLogPath(pat);
    }

    public void offLog() {
        Logging = false;
    }

    private void info(String msg) {
        if (Logging) {
            Log.info(msg);
        }
    }

    private void addLogPath(String pat) {
        try {
            Path fileToDeletePath = Paths.get(pat);
            Files.delete(fileToDeletePath);
        } catch (IOException ignored) {

        }

        try {
            FileHandler fh = new FileHandler(pat, false);
            fh.setFormatter(new SimFormatter());
            Log.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRecord() {
        Recording = false;
    }

    public void offRecord() {
        Recording = true;
    }

    public void setSeed(long seed) {
        // todo
    }

    public void simulate(IY0 y0, double fr, double to, double dt) throws JSONException {
        Time = fr;
        Model.initialise(Time, y0);
        dealWithDisclosures(fr, null);
        if (Recording) {
            Model.initialiseObservations(fr);
            Model.pushObservations(fr);
        }
        update(to, dt);
    }

    public void update(double forward, double dt) throws JSONException {
        dealWithDisclosures(Time, null);

        LinkedList<Double> ts = seq(Time, forward, dt);
        double f, t=ts.poll();
        while (!ts.isEmpty()) {
            f = t;
            t = ts.poll();
            if (Recording) {
                step(f, (f+t)/2);
                Model.captureMidTermObservations(t);
                step((f+t)/2, t);
                Model.updateObservations(t);
                Model.pushObservations(t);
            } else {
                step(f, t);
            }
        }
    }

    private void step(double t, double end) throws JSONException {
        double tx = t, ti;
        while (tx < end) {
            try {
                Model.collectRequests();
                List<Request> rs = Model.getScheduler().getRequests();

                if (rs.isEmpty()) break;
                ti = rs.get(0).getTime();
                if (ti > end) break;

                tx = ti;
                rs.forEach(req-> info(req.toLog()));

                Model.fetchRequests(rs);
                Model.synchroniseRequestTime(ti);
                Model.executeRequests();
                dealWithDisclosures(tx, rs);
            } catch (Exception e) {
                dealWithDisclosures(tx, null);
            }
            Model.exitCycle();
        }
        Model.exitCycle();
        Time = end;
        Model.setTimeEnd(end);
    }

    private void dealWithDisclosures(double ti, List<Request> requests) throws JSONException {
        List<Disclosure> ds;
        if (requests != null) {
            ds = requests.stream().map(Request::disclose).collect(Collectors.toList());
        } else {
            ds = new ArrayList<>();
        }

        ds.addAll(Model.collectDisclosure());
        ds = ds.stream().filter(d -> !d.getSource().equals(Model.getName())).collect(Collectors.toList());
        ds.forEach(dis-> info(dis.toLog()));

        Map<Disclosure, AbsSimModel> dm;
        while (!ds.isEmpty()) {
            dm = new LinkedHashMap<>();
            for (Disclosure d: ds) {
                dm.put(d.downScale().getSecond(), findModel(d));
            }
            Model.fetchDisclosures(dm, ti);
            ds = Model.collectDisclosure();
            ds = ds.stream().filter(d -> !d.getSource().equals(Model.getName())).collect(Collectors.toList());
            ds.forEach(dis-> info(dis.toLog()));
        }
    }

    private AbsSimModel findModel(Disclosure dis) {
        String adr = dis.getAddress();
        if (LazyModels.containsKey(adr)) {
            return LazyModels.get(adr);
        } else {
            AbsSimModel m = Model;
            List<String> where = new LinkedList<>(dis.Where);
            for (int i = where.size() - 2; i >= 0; i--) {
                m = ((BranchModel) m).getModel(where.get(i));
            }
            LazyModels.put(adr, m);
            return m;
        }
    }

    private LinkedList<Double> seq(double fr, double to, double by) {
        LinkedList<Double> s = new LinkedList<>();
        double ti = fr;
        while(ti < to) {
            s.add(ti);
            ti += by;
        }
        s.add(to);
        return s;
    }

    public static TimeSeries simulate(AbsSimModel model, IY0 y0,
                                      double fr, double to, double dt,
                                      boolean rec) throws Exception {
        Simulator sim = new Simulator(model, rec);
        sim.simulate(y0, fr, to, dt);
        if (rec) {
            return model.getObserver().getTimeSeries();
        } else {
            return null;
        }

    }

    public static TimeSeries update(AbsSimModel model,
                                                   double fr, double to, double dt,
                                                   boolean rec) throws Exception {
        Simulator sim = new Simulator(model, rec);
        sim.Time = fr;
        sim.update(to, dt);
        if (rec) {
            return model.getObserver().getTimeSeries();
        } else {
            return null;
        }
    }

}
